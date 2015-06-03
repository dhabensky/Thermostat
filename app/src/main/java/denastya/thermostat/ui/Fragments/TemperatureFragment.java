package denastya.thermostat.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import denastya.thermostat.R;
import denastya.thermostat.core.ModeSettings;
import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Model;
import denastya.thermostat.ui.Watchers.OverridenWatcher;
import denastya.thermostat.ui.Watchers.TempWatcher;
import denastya.thermostat.ui.Watchers.TimeWatcher;

/**
 * Created by admin on 01.06.2015.
 */
public class TemperatureFragment extends BaseFragment {

    CheckBox checkBox;
    Button overrideBtn;
    OverridenWatcher watcher;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout._2temperature, container, false);

        TextView curTemp = findTextView(R.id.curTemp);
        TextView modeTemp = findTextView(R.id.modeTemp);
        TextView modeSwitch = findTextView(R.id.modeSwitchTime);
        TextView nextModeTemp = findTextView(R.id.nextModeTemp);

        Model.getCurrentTemp().attachWatcher(new TempWatcher(curTemp));
        Model.getCurrentUsage().attachWatcher(new TempWatcher(modeTemp));
        Model.getNextUsage().attachWatcher(new TimeWatcher(modeSwitch));
        Model.getNextUsage().attachWatcher(new TempWatcher(nextModeTemp));


        checkBox = findCheckBox(R.id.checkBox);
        overrideBtn = findButton(R.id.overrideBtn);

        watcher = new OverridenWatcher(this);
        Model.attachOverridenWatcher(watcher);

        return rootView;
    }

    @Override
    public void update() {

        checkBox.setChecked(Model.isSwitchBlocked());

        overrideBtn.setText(Model.isOverriden() ? "Cancel" : "Override...");

        TextView tv1 = findTextView(R.id.nextSwitchLabel);
        TextView tv2 = findTextView(R.id.nextModeTemp);
        TextView tv3 = findTextView(R.id.modeSwitchTime);
        TextView tv4 = findTextView(R.id.textView22);

        if (Model.isOverriden()) {

            ModeUsage mode = new ModeUsage();
            mode.setSettings(Model.getSettings(ModeSettings.Period.OVERRIDE));
            Model.setCurrentUsage(mode);

            overrideBtn.setText("Cancel");
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(Model.isSwitchBlocked());
            if (checkBox.isChecked()) {
                tv1.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                tv3.setVisibility(View.INVISIBLE);
                tv4.setVisibility(View.INVISIBLE);
            }
            else {
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                tv3.setVisibility(View.VISIBLE);
                tv4.setVisibility(View.VISIBLE);
            }
        }
        else {

            if (Model.overridenUsage != null) {
                Model.setCurrentUsage(Model.overridenUsage);
            }

            overrideBtn.setText("Override...");
            checkBox.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
            tv4.setVisibility(View.VISIBLE);
        }

    }

}
