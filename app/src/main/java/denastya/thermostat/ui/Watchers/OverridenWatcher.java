package denastya.thermostat.ui.Watchers;

import android.widget.Button;

import denastya.thermostat.core.Watchers.BooleanWatcher;
import denastya.thermostat.ui.Fragments.TemperatureFragment;

/**
 * Created by admin on 03.06.2015.
 */
public class OverridenWatcher implements BooleanWatcher {

    private TemperatureFragment screen;


    public OverridenWatcher(TemperatureFragment screen) {
        this.screen = screen;
    }


    @Override
    public void onChange(boolean value) {
        screen.update();
    }

}
