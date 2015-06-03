package denastya.thermostat.ui.Fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by admin on 01.06.2015.
 */
public class BaseFragment extends Fragment {

    protected View rootView;


    public void update() {

    }

    public View getRootView() {
        return rootView;
    }

    public View findById(int id) {
        return rootView.findViewById(id);
    }

    public TextView findTextView(int id) {
        return (TextView)findById(id);
    }

    public NumberPicker findNumPicker(int id) {
        return (NumberPicker)findById(id);
    }

    public CheckBox findCheckBox(int id) {
        return (CheckBox)findById(id);
    }

    public ListView findListView(int id) {
        return (ListView)findById(id);
    }

    public Spinner findSpinner(int id) {
        return (Spinner)findById(id);
    }

    public Button findButton(int id) {
        return (Button)findById(id);
    }

}
