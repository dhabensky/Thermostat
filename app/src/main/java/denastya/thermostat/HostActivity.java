package denastya.thermostat;

import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import denastya.thermostat.core.DaySchedule;
import denastya.thermostat.core.Schedule;
import denastya.thermostat.ui.Fragments.BaseFragment;
import denastya.thermostat.ui.Fragments.ScheduleFragment;
import denastya.thermostat.ui.Fragments.SettingsFragment;
import denastya.thermostat.ui.Fragments.TemperatureFragment;
import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Model;
import denastya.thermostat.core.TimeEngine;
import denastya.thermostat.ui.Popups.SaveLoadPopup;
import denastya.thermostat.ui.Popups.TemperaturePickerPopup;
import denastya.thermostat.ui.Popups.TimePickerPopup;


public class HostActivity extends ActionBarActivity implements ActionBar.TabListener {

    private TemperaturePickerPopup popup;
    private TimePickerPopup timePopup;
    private SaveLoadPopup saveLoadPopup;
    private Thread thread = null;
    private final AtomicBoolean post = new AtomicBoolean();

    final Runnable run = new Runnable() {
        @Override
        public void run() {
            TimeEngine.WeekTime t = Model.timeEngine.getWeekTime();

            ModeUsage current = new ModeUsage();
            current.setStartTime(Model.timeEngine.getWeekTime());
            Model.schedule.switchMode(t.days, current);

            float power = 0.5f;
            float dtime = 0.5f;
            float delta = Model.getCurrentTemp().getTemperature() - Model.getCurrentUsage().getSettings().getTemperature();
            float res = Math.min(power * dtime, Math.abs(delta));
            Model.getCurrentTemp().setTemperature(Model.getCurrentTemp().getTemperature() - res * Math.signum(delta));
        }
    };


    public void onOverflowPressed(View v) {
        showSaveLoadPopup();
    }

    public void onMinusPressed(View v) {
        ScheduleFragment scheduleFragment = mSectionsPagerAdapter.getScheduleFragment();
        scheduleFragment.removeLast();
        scheduleFragment.update();
        scheduleFragment.scrollDown();
    }

    public void onPlusPressed(View v) {
        int day = mSectionsPagerAdapter.getScheduleFragment().getSelectedDay();
        ModeUsage usage = Model.schedule.daySchedules[day].getUsages().last();
        showTimePopup(usage);
    }


    public void toggleOverride(View v) {

        if (!Model.isOverriden()) {
            showTemperaturePopup(ModeSettings.Period.OVERRIDE);
        }
        else {
            Model.setOverriden(false);
            mSectionsPagerAdapter.getTemperatureFragment().update();
        }
    }

    public void toggleBlock(View v) {
        Model.setSwitchBlocked(!Model.isSwitchBlocked());
        mSectionsPagerAdapter.getTemperatureFragment().update();
    }


    public void swipeToSettingsCurrent(View v) {
        Model.setEditMode(Model.getCurrentUsage().getSettings());
        showTemperaturePopup(Model.getEditMode().getPeriod());
    }

    public void swipeToSettingsNext(View v) {
        Model.setEditMode(Model.getNextUsage().getSettings());
        showTemperaturePopup(Model.getEditMode().getPeriod());
    }

    public void swipeToSchedule(View v) {
        mViewPager.setCurrentItem(2);
    }


    public void onAdjustModeRequest(View v) {

        if (v.getTag().equals("dayTemp")) {
            showTemperaturePopup(ModeSettings.Period.DAY);
        }
        else if (v.getTag().equals("nightTemp")) {
            showTemperaturePopup(ModeSettings.Period.NIGHT);
        }
    }

    public void onAdjustModeConfirm(View v) {

        float temp = ((NumberPicker) popup.getContentView().findViewById(R.id.integralPicker)).getValue() +
                ((NumberPicker) popup.getContentView().findViewById(R.id.fractionalPicker)).getValue() / 10.0f;

        Model.getAdjusting().setTemperature(temp);

        if (Model.getAdjusting().getPeriod() == ModeSettings.Period.OVERRIDE) {
            Model.setOverriden(true);
            mSectionsPagerAdapter.getTemperatureFragment().update();
        }

        popup.dismiss();
        popup = null;
    }


    public void showTemperaturePopup(ModeSettings.Period period) {

        Model.setAdjusting(period);

        View v = inflateLayout(R.layout._4popup);
        Point size = getWindowSize();

        v.measure(
                View.MeasureSpec.makeMeasureSpec((int)(size.x * 0.9), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec((int)(size.y * 0.7), View.MeasureSpec.AT_MOST)
        );

        popup = new TemperaturePickerPopup(v.getMeasuredWidth(), v.getMeasuredHeight(), v);
        popup.onCreate();
        popup.showAtLocation(new LinearLayout(this), Gravity.CENTER, 0, getStatusBarHeight());
    }

    public void showTimePopup(ModeUsage usage) {

        View v = inflateLayout(R.layout._5timepopup);
        Point size = getWindowSize();

        v.measure(
                View.MeasureSpec.makeMeasureSpec((int)(size.x * 0.9), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec((int)(size.y * 0.7), View.MeasureSpec.AT_MOST)
        );

        timePopup = new TimePickerPopup(v.getMeasuredWidth(), v.getMeasuredHeight(), v, usage);
        timePopup.onCreate();
        timePopup.showAtLocation(new LinearLayout(this), Gravity.CENTER, 0, getStatusBarHeight());
    }

    public void showSaveLoadPopup() {

        View v = inflateLayout(R.layout._6saveload);
        Point size = getWindowSize();

        v.measure(
                View.MeasureSpec.makeMeasureSpec((int)(size.x * 0.9), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec((int)(size.y * 0.7), View.MeasureSpec.AT_MOST)
        );

        saveLoadPopup = new SaveLoadPopup(v.getMeasuredWidth(), v.getMeasuredHeight(), v);
        saveLoadPopup.onCreate();
        saveLoadPopup.showAsDropDown(findViewById(R.id.imageButton));
        //saveLoadPopup.showAtLocation(new LinearLayout(this), Gravity.CENTER, 0, getStatusBarHeight());
    }


    public void onAddNewModeUsage(View v) {

        int day = (((Spinner) Model.activity.getWindow().findViewById(R.id.spinner)).getSelectedItemPosition() + 1) % 7;
        ModeUsage usage = Model.schedule.daySchedules[day].getUsages().last();

        ModeUsage newMode = new ModeUsage();
        newMode.setSettings(
                Model.getSettings(usage.getSettings().getPeriod() == ModeSettings.Period.DAY ?
                        ModeSettings.Period.NIGHT : ModeSettings.Period.DAY));

        int hours = ((NumberPicker) timePopup.getContentView().findViewById(R.id.integralPicker)).getValue() - 1;
        if (((NumberPicker) timePopup.getContentView().findViewById(R.id.numberPicker)).getValue() == 2)
            hours += 12;
        int mins = ((NumberPicker) timePopup.getContentView().findViewById(R.id.fractionalPicker)).getValue() - 1;

        TimeEngine.WeekTime t = new TimeEngine.WeekTime((byte)(usage.getStart().days), (byte)(hours), (byte)mins, (byte)0);
        newMode.setStartTime(t);

        if (newMode.compareTo(usage) > 0) {

            Model.schedule.daySchedules[day].add(newMode);

            timePopup.dismiss();
            timePopup = null;
            mSectionsPagerAdapter.getScheduleFragment().update();
            mSectionsPagerAdapter.getScheduleFragment().scrollDown();
        }
        else {
            Toast.makeText(getApplicationContext(), "must be greater than " + usage.getStartString().substring(4), Toast.LENGTH_LONG).show();
        }
    }


    public View inflateLayout(int layoutId) {

        LinearLayout ll = new LinearLayout(this);
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(layoutId, ll);
    }

    public Point getWindowSize() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_activity);

        Model.init();
        Model.activity = this;
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                try {
                    mSectionsPagerAdapter.getItem(position).update();
                }
                catch (Exception ex) {
                    Log.d("RRR", ex.toString());
                }
            }
        });

        mViewPager.setCurrentItem(1);


        Calendar c = Calendar.getInstance();

        Model.timeEngine.setTicks(
                c.get(Calendar.DAY_OF_WEEK),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND)
        );
        Model.timeEngine.setTimeFactor(300);
        Model.timeEngine.start();

        final Handler handler = new Handler(getBaseContext().getMainLooper());


        post.set(true);

        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    while (post.get()) {
                        handler.post(run);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        post.set(false);
        thread = null;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SettingsFragment settingsFragment;
        TemperatureFragment temperatureFragment;
        ScheduleFragment scheduleFragment;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public SettingsFragment getSettingsFragment() {
            if (settingsFragment == null)
                settingsFragment = new SettingsFragment();
            return settingsFragment;
        }

        public TemperatureFragment getTemperatureFragment() {
            if (temperatureFragment == null)
                temperatureFragment = new TemperatureFragment();
            return temperatureFragment;
        }

        public ScheduleFragment getScheduleFragment() {
            if (scheduleFragment == null)
                scheduleFragment = new ScheduleFragment();
            return scheduleFragment;
        }

        @Override
        public BaseFragment getItem(int position) {
            switch (position) {
                case 0:
                    return getSettingsFragment();
                case 1:
                    return getTemperatureFragment();
                case 2:
                    return getScheduleFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
            }
            return null;
        }
    }

}
