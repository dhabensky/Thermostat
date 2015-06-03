package denastya.thermostat.ui.Popups;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import denastya.thermostat.R;
import denastya.thermostat.core.Model;

/**
 * Created by admin on 01.06.2015.
 */
public class TemperaturePickerPopup extends BasePopup {

    public TemperaturePickerPopup(int width, int height, View view) {
        super(width, height, view);
    }


    @Override
    public void onCreate() {

        TextView t = (TextView)rootView.findViewById((R.id.modeName));
        t.setText(Model.getAdjustingString());

        int min = 5;
        int max = 30;

        class Shit {
            public boolean skip;
        }
        final Shit shit = new Shit();

        NumberPicker numpick = (NumberPicker)rootView.findViewById(R.id.integralPicker);
        numpick.setMinValue(min);
        numpick.setMaxValue(max);
        numpick.setWrapSelectorWheel(false);
        numpick.setValue(Model.getAdjusting().getTemperatureInt());

        final NumberPicker intpick = numpick;

        numpick = (NumberPicker)rootView.findViewById(R.id.fractionalPicker);
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

}
