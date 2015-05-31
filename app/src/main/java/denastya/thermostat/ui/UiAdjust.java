package denastya.thermostat.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import denastya.thermostat.R;
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


        class DropDownListener implements AdapterView.OnItemSelectedListener {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }


        String[] days = {
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };

        Spinner spinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                Model.activity, R.array.days_array, R.layout.spinner_layout);



        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(new DropDownListener());

        //adapter.notifyDataSetChanged();
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
