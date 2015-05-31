package denastya.thermostat.ui.Watchers;

import android.widget.TextView;

import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.Model;

/**
 * Created by admin on 30.05.2015.
 */
public class TempWatcher implements denastya.thermostat.core.Watchers.SettingsTempWatcher {

    private TextView textView;


    public TempWatcher(TextView textView) {
        this.textView = textView;
    }


    @Override
    public void onChange(ModeSettings settings) {
        textView.setText(settings.getTemperatureString());
    }

}
