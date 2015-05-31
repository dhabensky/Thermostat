package denastya.thermostat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Model;
import denastya.thermostat.core.RowSchedule;
import denastya.thermostat.core.TimeEngine;
import denastya.thermostat.ui.UiAdjust;


public class HostActivity extends ActionBarActivity implements ActionBar.TabListener {

    private PopupWindow popup;
    private Thread thread = null;
    private final AtomicBoolean post = new AtomicBoolean();

    final Runnable run = new Runnable() {
        @Override
        public void run() {
            TimeEngine.WeekTime t = Model.timeEngine.getWeekTime();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, t.days + 1);
            c.set(Calendar.HOUR, t.hours);
            c.set(Calendar.MINUTE, t.mins);
            c.set(Calendar.SECOND, t.secs);

            ModeUsage current = new ModeUsage();
            current.setStartTime(Model.timeEngine.getWeekTime());
            Model.schedule.switchMode(t.days, current);

//            Model.getNextUsage().setEnd(c);

            float power = 0.5f;
            float dtime = 0.5f;
            float delta = Model.getCurrentTemp().getTemperature() - Model.getCurrentUsage().getSettings().getTemperature();
            float res = Math.min(power * dtime, Math.abs(delta));
            Model.getCurrentTemp().setTemperature(Model.getCurrentTemp().getTemperature() - res * Math.signum(delta));

        }
    };


    public void toggleOverride(View v) {

        if (!Model.isOverriden()) {
            adjustMode(ModeSettings.Period.TEMP_OVERRIDE);
        }
        else {
            Model.setOverriden(false);
            ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
            UiAdjust.adjustTemperatureScreenItems(root);
        }
    }

    public void toggleBlock(View v) {
        Model.setSwitchBlocked(!Model.isSwitchBlocked());

        if (Model.isSwitchBlocked()) {
            ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
            ((TextView)root.findViewById(R.id.nextSwitchLabel)).setVisibility(View.INVISIBLE);
            ((TextView)root.findViewById(R.id.nextModeTemp)).setVisibility(View.INVISIBLE);
            ((TextView)root.findViewById(R.id.modeSwitchTime)).setVisibility(View.INVISIBLE);
            ((TextView)root.findViewById(R.id.textView22)).setVisibility(View.INVISIBLE);
        }
        else {
            ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
            ((TextView)root.findViewById(R.id.nextSwitchLabel)).setVisibility(View.VISIBLE);
            ((TextView)root.findViewById(R.id.nextModeTemp)).setVisibility(View.VISIBLE);
            ((TextView)root.findViewById(R.id.modeSwitchTime)).setVisibility(View.VISIBLE);
            ((TextView)root.findViewById(R.id.textView22)).setVisibility(View.VISIBLE);
        }
    }

    public void swipeToSettingsCurrent(View v) {
        Model.setEditMode(Model.getCurrentUsage().getSettings());
        adjustMode(Model.getEditMode().getPeriod());
    }

    public void swipeToSettingsNext(View v) {
        Model.setEditMode(Model.getNextUsage().getSettings());
        adjustMode(Model.getEditMode().getPeriod());
    }

    public void swipeToSchedule(View v) {
        mViewPager.setCurrentItem(2);
    }

    public void saveSettings(View v) {

        float temp = ((NumberPicker) popup.getContentView().findViewById(R.id.integralPicker)).getValue() +
                ((NumberPicker) popup.getContentView().findViewById(R.id.fractionalPicker)).getValue() / 10.0f;

        Model.getAdjusting().setTemperature(temp);

        if (Model.getAdjusting().getPeriod() == ModeSettings.Period.TEMP_OVERRIDE) {
            Model.setOverriden(true);
        }

        popup.dismiss();
        popup = null;
    }

    public void adjustMode(View v) {

        if (v.getTag().equals("dayTemp")) {
            adjustMode(ModeSettings.Period.DAY);
        }
        else if (v.getTag().equals("nightTemp")) {
            adjustMode(ModeSettings.Period.NIGHT);
        }
    }

    public void adjustMode(ModeSettings.Period period) {

        Model.setAdjusting(period);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        LinearLayout ll = new LinearLayout(this);
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout._4popup, ll);
        v.measure(
                View.MeasureSpec.makeMeasureSpec((int)(size.x * 0.9), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec((int)(size.y * 0.7), View.MeasureSpec.AT_MOST)
        );

        popup = new PopupWindow(v.getMeasuredWidth(), v.getMeasuredHeight());
        UiAdjust.onPopupCreate(v);

        popup.setContentView(v);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setOutsideTouchable(true);

//            if (Model.needReturn) {
//                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        new Thread(new Runnable() {
//
//                            long start = System.currentTimeMillis();
//
//                            @Override
//                            public void run() {
//
//                                while (System.currentTimeMillis() < start + 300) {
//                                    try {
//                                        Thread.sleep(50);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                mViewPager.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (mViewPager.getCurrentItem() == 0)
//                                            mViewPager.setCurrentItem(1);
//                                        popup = null;
//                                        //Model.needReturn = false;
//                                    }
//                                });
//                            }
//                        }).start();
//                    }
//                });
//            }

            //popup.setAnimationStyle(R.style.PopupWindowAnimation); // kills my smartphone :((
        popup.showAtLocation(new LinearLayout(this), Gravity.CENTER, 0, getStatusBarHeight());
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
    SectionsPagerAdapter mSectionsPagerAdapter;

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

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                if (position == 0 && positionOffsetPixels == 0 && Model.getEditMode() != null) {
//                    adjustMode(Model.getEditMode().getPeriod());
//                    Model.setEditMode(null);
//                    Model.needReturn = true;
//                }
//                else {
//                    synchronized (this) {
//                        Model.needReturn = false;
//                        if (popup != null) {
//                            popup.dismiss();
//                            popup = null;
//                        }
//                    }
//                }
//            }
        });

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
        mViewPager.setCurrentItem(1);


        Calendar c = Calendar.getInstance();

        Log.d("RRR", c.get(Calendar.DAY_OF_WEEK) + "");

        Model.timeEngine.setTicks(
                c.get(Calendar.DAY_OF_WEEK) + 4,
                c.get(Calendar.HOUR),
                c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND)
        );
        Model.timeEngine.setTimeFactor(1500);
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        post.set(false);
        thread = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;

            int num = (int)(getArguments().get(ARG_SECTION_NUMBER));
            switch (num) {
                case 1:
                    rootView = inflater.inflate(R.layout._1settings, container, false);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout._2temperature, container, false);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout._3schedule, container, false);
                    break;
            }

            UiAdjust.onScreenCreate(num, rootView);

            return rootView;
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            RowSchedule.DayPart dp = (hourOfDay < 12) ? RowSchedule.DayPart.AM : RowSchedule.DayPart.PM;
            boolean day = ((RadioButton)getActivity().getWindow().findViewById(R.id.radioButtonDay)).isChecked();
            RowSchedule rs = new RowSchedule(hourOfDay, minute, dp, (day ? ModeSettings.Period.DAY : ModeSettings.Period.NIGHT));
            rowScheduleList.add(rs);
            Collections.sort(rowScheduleList, RowSchedule.getComparator());
        }
    }

    public static class ListViewLoader extends ListActivity {

        private static final int CM_DELETE_ID = 1;

        final String ATTRIBUTE_NAME_TEXT = "text";

        ListView lvSimple;
        SimpleAdapter sAdapter;
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(rowScheduleList.size());


        /** Called when the activity is first created. */
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Map<String, Object> m;
            for (int i = 0; i < rowScheduleList.size(); i++) {
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_TEXT, rowScheduleList.get(i));
                data.add(m);
            }
            // ������ ���� ���������, �� ������� ����� �������� ������
            String[] from = { ATTRIBUTE_NAME_TEXT};
            // ������ ID View-�����������, � ������� ����� ��������� ������
            int[] to = { R.id.tvText};

            // ������� �������
            sAdapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_1, from, to);

            // ���������� ������ � ����������� ��� �������
            lvSimple = (ListView) findViewById(R.id.listView);
            lvSimple.setAdapter(sAdapter);
            registerForContextMenu(lvSimple);
        }

        public void onButtonClick(View v) {
            // ����������, ��� ������ ����������
            sAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.add(0, CM_DELETE_ID, 0, "Delete");
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            if (item.getItemId() == CM_DELETE_ID) {
                // �������� ���� � ������ ������
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                // ������� Map �� ���������, ��������� ������� ������ � ������
                rowScheduleList.remove(acmi.position);
                // ����������, ��� ������ ����������
                sAdapter.notifyDataSetChanged();
                return true;
            }
            return super.onContextItemSelected(item);
        }
    }


    public static ModeSettings.Period onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        switch (view.getId()) {
            case R.id.radioButtonDay:
                if (checked) {
                    return ModeSettings.Period.DAY;
                }
            case R.id.radioButtonNight:
                if (checked) {
                    return ModeSettings.Period.NIGHT;
                }
        }
        return null;
    }

    public static List<RowSchedule> rowScheduleList = new ArrayList<>();


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(HostActivity.this.getFragmentManager(), "timePicker");
    }

    public void onRadioButtonNightClicked(View view) {
        //addbutton.setvisible(true)
        Button addbutton = (Button)findViewById(R.id.addbutton);
        if (addbutton.getVisibility() != View.VISIBLE)
            addbutton.setVisibility(View.VISIBLE);
    }

    public void onRadioButtonDayClicked(View view) {
        //addbutton.setvisible(true)
        Button addbutton = (Button)findViewById(R.id.addbutton);
        if (addbutton.getVisibility() != View.VISIBLE)
            addbutton.setVisibility(View.VISIBLE);
    }

}
