package denastya.thermostat.ui;

import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import denastya.thermostat.R;
import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.Model;
import denastya.thermostat.core.Watchers.SettingsPeriodWatcher;
import denastya.thermostat.ui.Watchers.PeriodWatcher;
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
        Model.getCurrentUsage().attachWatcher(new TimeWatcher(modeSwitch));
        Model.getNextUsage().attachWatcher(new TempWatcher(nextModeTemp));
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

    public static void onScreenCreate(int index, View view) {
        switch (index) {
            case SETTINGS:
                onSettingsScreenCreate(view);
                break;
            case 2:
                onTemperatureScreenCreate(view);
                break;
            case 3:
                break;
            case POPUP:
                onPopupCreate(view);
                break;
        }
    }

}
