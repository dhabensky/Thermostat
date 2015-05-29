package denastya.thermostat.ui;

import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import denastya.thermostat.R;
import denastya.thermostat.core.ModeManager;
import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.Static;

/**
 * Created by admin on 29.05.2015.
 */
public class UiAdjust {

    public static void adjustSettingsPopup(View view) {

        TextView t = (TextView)view.findViewById((R.id.modeName));
        t.setText(Static.mode + " mode");

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
        numpick.setValue(Static.adjustingMode.getTemperatureInt());

        final NumberPicker intpick = numpick;

        numpick = (NumberPicker)view.findViewById(R.id.fractionalPicker);
        numpick.setMinValue(0);
        numpick.setMaxValue(9);
        numpick.setValue(Static.adjustingMode.getTemperatureFrac());
        numpick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            long millis = System.currentTimeMillis();
            boolean jumpUp = false;
            boolean jumpDown = false;
            int initial;
            boolean fromTop;

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


    public static void adjustSettingsScreen(View view) {


        TextView t = (TextView)view.findViewById((R.id.dayTemp));
        t.setText(ModeManager.getSettings(ModeSettings.Period.DAY).getTemperatureString());

        t = (TextView)view.findViewById((R.id.nightTemp));
        t.setText(ModeManager.getSettings(ModeSettings.Period.NIGHT).getTemperatureString());

    }

}
