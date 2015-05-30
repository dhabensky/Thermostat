package denastya.thermostat.ui.Watchers;

import android.view.ViewGroup;

import denastya.thermostat.core.Watchers.BooleanWatcher;
import denastya.thermostat.ui.UiAdjust;

/**
 * Created by admin on 30.05.2015.
 */
public class OverridenWatcher implements BooleanWatcher {

    private ViewGroup view;


    public OverridenWatcher(ViewGroup view) {
        this.view = view;
    }


    public void onChange(boolean overriden) {
        UiAdjust.adjustTemperatureScreenItems(view);
    }

}
