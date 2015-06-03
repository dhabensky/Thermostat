package denastya.thermostat.ui.Popups;

import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import denastya.thermostat.R;
import denastya.thermostat.core.ModeUsage;

/**
 * Created by admin on 01.06.2015.
 */
public class TimePickerPopup extends BasePopup {

    private ModeUsage minTime;


    public TimePickerPopup(int width, int height, View view, ModeUsage minTime) {
        super(width, height, view);
        this.minTime = minTime;
    }


    @Override
    public void onCreate() {

        TextView t = (TextView)rootView.findViewById((R.id.modeName));
        String modeName = minTime.getSettings().isDay() ? "night" : "day";
        t.setText("Start " + modeName + " at:");

        int min = minTime.getStart().hours;
        int max = 23;

        String[] nums = new String[12];
        for (int i = 0; i <= 11; i++) {
            int val = i;
//            if (val == 0)
//                val = 12;
            nums[i] = (val < 10 ? "0" + val : "" + val);
            //Log.d("RRR", nums[i - min]);
        }

        String[] mins = new String[60];
        for (int i = 0; i < 60; i++) {
            String s = "0" + i;
            mins[i]  = s.substring(s.length() - 2);
        }

        class Shit {
            public boolean skip;
        }
        final Shit shit = new Shit();

        NumberPicker numpick = (NumberPicker)rootView.findViewById(R.id.integralPicker);
        numpick.setMinValue(1);
        numpick.setMaxValue(12);
        numpick.setDisplayedValues(nums);
        numpick.setValue(minTime.getStart().hours % 12 + 1);

        final NumberPicker intpick = numpick;

        numpick = (NumberPicker)rootView.findViewById(R.id.fractionalPicker);
        numpick.setMinValue(1);
        numpick.setMaxValue(60);
        numpick.setDisplayedValues(mins);
        numpick.setValue(minTime.getStart().mins + 1);
        numpick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                int initial = intpick.getValue();

                Log.d("RRR", newVal + "");

                if (oldVal == 1 && newVal == 60) {
                    intpick.setValue(initial - 1);
                } else if (oldVal == 60 && newVal == 1) {
                    intpick.setValue(initial + 1);
                } else {
                    shit.skip = false;
                }
            }
        });


        final NumberPicker ampm = (NumberPicker)rootView.findViewById(R.id.numberPicker);
        ampm.setMinValue(1);
        ampm.setMaxValue(2);
        ampm.setDisplayedValues(new String[] { "AM", "PM"});
        ampm.setValue(minTime.getStart().hours >= 12 ? 2 : 1);
    }
}
