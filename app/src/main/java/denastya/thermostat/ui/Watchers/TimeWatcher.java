package denastya.thermostat.ui.Watchers;

import android.widget.TextView;

import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Watchers.UsageTimeWatcher;

/**
 * Created by admin on 30.05.2015.
 */
public class TimeWatcher implements UsageTimeWatcher {

    private TextView textView;


    public TimeWatcher(TextView textView)  {
        this.textView = textView;
    }


    @Override
    public void onChange(ModeUsage usage) {
        textView.setText(usage.getEndString());
    }

}
