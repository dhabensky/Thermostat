package denastya.thermostat.ui.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

import denastya.thermostat.R;
import denastya.thermostat.core.DaySchedule;
import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Model;

/**
 * Created by admin on 01.06.2015.
 */
public class ScheduleFragment extends BaseFragment {

    ListView listView;
    ArrayAdapter<CharSequence> listAdapter;
    Spinner spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout._3schedule, container, false);

        spinner = findSpinner(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                Model.activity, R.array.days_array, R.layout.spinner_layout);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSchedule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView = findListView(R.id.listView);
        listAdapter = new ArrayAdapter<CharSequence>(
                Model.activity,
                R.layout.simple_list_item_1, new String[0]);
        listView.setAdapter(listAdapter);

        return rootView;
    }

    @Override
    public void update() {
        updateSchedule();
    }


    public void loadSchedule(int day) {

        DaySchedule ds = Model.schedule.daySchedules[day];

        final String[] content = new String[ds.getUsages().size()];

        int i = 0;
        for (ModeUsage usage : ds.getUsages()) {
            String s = usage.getStartString().substring(4);

            int h = usage.getStart().hours;
            h %= 12;
            if (h == 0)
                h = 0;
            if ((h + "").length() == 1)
                s = "  " + s;
            s += "    " + (usage.getSettings().isDay() ? "(day)" : "(night)");
            content[i++] = s;
        }

        class CustomAdapter<T> extends ArrayAdapter<T> {

            CustomAdapter(Context context, int resource, T[] objects) {
                super(context, resource, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Log.d("RRRR", position + " " + content[position]);
                if (content[position].endsWith("(night)"))
                    v.setBackgroundColor(Color.parseColor("#E7E7E7"));
                else
                    v.setBackgroundColor(Color.parseColor("#EEEEEE"));
                return v;
            }
        }

        listAdapter = new CustomAdapter<CharSequence>(
                Model.activity,
                android.R.layout.simple_list_item_1, content);
        listView.setAdapter(listAdapter);
    }

    public void updateSchedule() {

        loadSchedule((findSpinner(R.id.spinner).getSelectedItemPosition() + 1) % 7);

        int count = findListView(R.id.listView).getCount();

        findButton(R.id.button3).setEnabled(count < 10);
        findButton(R.id.button2).setEnabled(count > 1);
    }

    public void removeLast() {
        int day = (findSpinner(R.id.spinner).getSelectedItemPosition() + 1) % 7;
        Model.schedule.daySchedules[day].remove(Model.schedule.daySchedules[day].getUsages().last());
    }

    public void scrollDown() {
        listView.setSelection(listView.getCount() - 1);
    }

    public int getSelectedDay() {
        return (findSpinner(R.id.spinner).getSelectedItemPosition() + 1) % 7;
    }

    public View getViewByPosition(int pos) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
