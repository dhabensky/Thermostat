package denastya.thermostat.ui;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Objects;

import denastya.thermostat.R;
import denastya.thermostat.core.DaySchedule;
import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Model;
import denastya.thermostat.ui.Watchers.OverridenWatcher;
import denastya.thermostat.ui.Watchers.TempWatcher;
import denastya.thermostat.ui.Watchers.TimeWatcher;

/**
 * Created by admin on 29.05.2015.
 */
public class UiAdjust {

    public static final int SETTINGS = 1;
    public static final int TEMPERATURE = 2;
    public static final int SCHEDULE = 3;
    public static final int POPUP = 4;

    public static void onSettingsScreenCreate(View view) {

        final TextView dayTemp = (TextView)view.findViewById((R.id.dayTemp));
        final TextView nightTemp = (TextView)view.findViewById((R.id.nightTemp));

        Model.getSettings(ModeSettings.Period.DAY).attachWatcher(new TempWatcher(dayTemp));
        Model.getSettings(ModeSettings.Period.NIGHT).attachWatcher(new TempWatcher(nightTemp));
    }

    public static void onTemperatureScreenCreate(View view) {

        TextView curTemp = (TextView)view.findViewById((R.id.curTemp));
        TextView modeTemp = (TextView)view.findViewById((R.id.modeTemp));
        TextView modeSwitch = (TextView)view.findViewById((R.id.modeSwitchTime));
        TextView nextModeTemp = (TextView)view.findViewById((R.id.nextModeTemp));

        Model.getCurrentTemp().attachWatcher(new TempWatcher(curTemp));
        Model.getCurrentUsage().attachWatcher(new TempWatcher(modeTemp));
        Model.getNextUsage().attachWatcher(new TimeWatcher(modeSwitch));
        Model.getNextUsage().attachWatcher(new TempWatcher(nextModeTemp));


        Model.attachOverridenWatcher(new OverridenWatcher((ViewGroup)view));
        adjustTemperatureScreenItems(view);
    }

    public static void adjustTemperatureScreenItems(View view) {

        CheckBox chb = (CheckBox)view.findViewById(R.id.checkBox);
        chb.setChecked(Model.isSwitchBlocked());

        Button overrideBtn = ((Button)view.findViewById(R.id.overrideBtn));
        overrideBtn.setText(Model.isOverriden() ? "Cancel" : "Override...");

        TextView tv1 = (TextView)view.findViewById(R.id.nextSwitchLabel);
        TextView tv2 = (TextView)view.findViewById(R.id.nextModeTemp);
        TextView tv3 = (TextView)view.findViewById(R.id.modeSwitchTime);
        TextView tv4 = (TextView)view.findViewById(R.id.textView22);

        if (Model.isOverriden()) {
            // fix here !!
            ModeUsage mode = new ModeUsage();
            mode.setSettings(Model.getSettings(ModeSettings.Period.OVERRIDE));
            Model.setCurrentUsage(mode);

            overrideBtn.setText("Cancel");
            chb.setVisibility(View.VISIBLE);
            chb.setChecked(Model.isSwitchBlocked());
            if (chb.isChecked()) {
                tv1.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                tv3.setVisibility(View.INVISIBLE);
                tv4.setVisibility(View.INVISIBLE);
            }
            else {
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                tv3.setVisibility(View.VISIBLE);
                tv4.setVisibility(View.VISIBLE);
            }
        }
        else {
            // fix here !!
            if (Model.overridenUsage != null) {
                Model.setCurrentUsage(Model.overridenUsage);
            }

            overrideBtn.setText("Override...");
            chb.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
            tv4.setVisibility(View.VISIBLE);
        }


    }

    public static void onPopupCreate(View view) {

        TextView t = (TextView)view.findViewById((R.id.modeName));
        t.setText(Model.getAdjustingString());

        int min = 10;
        int max = 30;

        class Shit {
            public boolean skip;
        }
        final Shit shit = new Shit();

        NumberPicker numpick = (NumberPicker)view.findViewById(R.id.integralPicker);
        numpick.setMinValue(min);
        numpick.setMaxValue(max);
        numpick.setWrapSelectorWheel(false);
        numpick.setValue(Model.getAdjusting().getTemperatureInt());

        final NumberPicker intpick = numpick;

        numpick = (NumberPicker)view.findViewById(R.id.fractionalPicker);
        numpick.setMinValue(0);
        numpick.setMaxValue(9);
        numpick.setValue(Model.getAdjusting().getTemperatureFrac());
        numpick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                int initial = intpick.getValue();

                if (oldVal == 0 && newVal == 9) {
                    if (initial == intpick.getMinValue())
                        shit.skip = true;
                    else
                        intpick.setValue(initial - 1);
                }
                else if (oldVal == 9 && newVal == 0) {
                    if (!shit.skip)
                        intpick.setValue(initial + 1);
                    else
                        shit.skip = false;
                }
                else {
                    shit.skip = false;
                }
            }
        });
    }

    public static void onScheduleScreenCreated(View view) {

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                Model.activity, R.array.days_array, R.layout.spinner_layout);


        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadSchedule((ListView)Model.activity.getWindow().findViewById(R.id.listView), position);
                Log.d("RRR", "" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Model.content = new String[1];
        Model.content[0] = "AXAXA";

        ListView listView = (ListView) view.findViewById(R.id.listView);
        ListAdapter listadapter = new ArrayAdapter<String>(
                Model.activity,
                android.R.layout.simple_list_item_1, Model.content);
        listView.setAdapter(listadapter);
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }


    public static void loadSchedule(ListView listView, int day) {

        DaySchedule ds = Model.schedule.daySchedules[day];

        String[] content = new String[ds.getUsages().size()];

        int i = 0;
        for (ModeUsage usage : ds.getUsages()) {
            String s = usage.getStartString().substring(4);

            int h = usage.getStart().hours;
            h %= 12;
            if (h == 0)
                h = 12;
            if ((h + "").length() == 1)
                s = "  " + s;
            s += "    " + (usage.getSettings().isDay() ? "(day)" : "(night)");
            content[i++] = s;
        }

        Model.content = content;

        ArrayAdapter<String> adapter = (ArrayAdapter<String>)listView.getAdapter();

        //adapter.clear();
        ListAdapter listadapter = new ArrayAdapter<String>(
                Model.activity,
                android.R.layout.simple_list_item_1, content);
        listView.setAdapter(listadapter);

//        for (String s : content)
//            adapter.add(s);
//
//        ((ArrayAdapter<String>)listView.getAdapter()).notifyDataSetChanged();
        //listView.getAdapter().
    }

    public static void onScreenCreate(int index, View view) {
        switch (index) {
            case SETTINGS:
                onSettingsScreenCreate(view);
                break;
            case 2:
                onTemperatureScreenCreate(view);
                break;
            case 3:
                onScheduleScreenCreated(view);
                break;
            case POPUP:
                onPopupCreate(view);
                break;
        }
    }

}
