package denastya.thermostat.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import denastya.thermostat.R;
import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.Model;
import denastya.thermostat.ui.Watchers.TempWatcher;

/**
 * Created by admin on 01.06.2015.
 */
public class SettingsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout._1settings, container, false);

        Model.getSettings(ModeSettings.Period.DAY).attachWatcher(new TempWatcher(
                findTextView(R.id.dayTemp)));

        Model.getSettings(ModeSettings.Period.NIGHT).attachWatcher(new TempWatcher(
                findTextView(R.id.nightTemp)));

        return rootView;
    }

}
