package denastya.thermostat.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.File;

import denastya.thermostat.R;

/**
 * Created by admin on 28.05.2015.
 */
public class SettingsTab {

    public SettingsTab() {

    }

    public void doMyDeeds(View view) {

        NumberPicker numpick = (NumberPicker)view.findViewById(R.id.numpick);

        int min = 22;
        int max = 33;
        String[] nums = new String[max - min + 1];
        for (int i = min; i <= max; i++)
            nums[i - min] = Integer.toString(i);


        numpick.setMinValue(1);
        numpick.setMaxValue(max - min + 1);
        numpick.setWrapSelectorWheel(false);
        numpick.setDisplayedValues(nums);
        numpick.setValue(1);
    }
}
