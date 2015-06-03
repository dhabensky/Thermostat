package denastya.thermostat.ui.Watchers;

import android.widget.TextView;

import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.Model;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;

/**
 * Created by admin on 30.05.2015.
 */
public class TempWatcher implements denastya.thermostat.core.Watchers.SettingsTempWatcher {

    public TextView textView;


    public TempWatcher(TextView textView) {
        this.textView = textView;
    }


    @Override
    public void onChange(ModeSettings settings) {
        textView.setText(settings.getTemperatureString());
    }

    public boolean equals(SettingsTempWatcher stw) {
        if (stw instanceof TempWatcher)
            return ((TempWatcher)stw).textView == textView;
        else
            return false;
    }
}
