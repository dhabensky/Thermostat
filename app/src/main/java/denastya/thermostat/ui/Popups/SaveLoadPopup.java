package denastya.thermostat.ui.Popups;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Iterator;

import denastya.thermostat.core.DaySchedule;
import denastya.thermostat.core.ModeUsage;
import denastya.thermostat.core.Model;
import denastya.thermostat.R;
import denastya.thermostat.core.Schedule;

/**
 * Created by admin on 03.06.2015.
 */
public class SaveLoadPopup extends BasePopup {

    public SaveLoadPopup(int width, int height, View view) {
        super(width, height, view);
    }

    public void onCreate() {


        ListView listView = ((ListView)rootView.findViewById(R.id.listView2));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    saveWeek();
                }
                else {
                    loadWeek();
                    Model.activity.mSectionsPagerAdapter.getScheduleFragment().update();
                }

                dismiss();
            }
        });
    }


    public void saveWeek() {
        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getPath() + "/schedule";

        Log.d("RRRR", "save");
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(Model.schedule);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadWeek() {
        Log.d("RRRR", "load");

        try {
            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/schedule");
            ObjectInputStream oin = new ObjectInputStream(fis);
            Schedule schedule = (Schedule)oin.readObject();

            int day = 0;
            for (DaySchedule ds : Model.schedule.daySchedules) {

                Iterator<ModeUsage> it = schedule.daySchedules[day].getUsages().iterator();
                for (ModeUsage mu : ds.getUsages()) {
                    if (it.hasNext()) {
                        ModeUsage m = it.next();
                        mu.startTime = m.startTime;
                        mu.setSettings(m.getSettings());
                    }
                    else
                        break;
                }

                while (it.hasNext())
                    ds.add(it.next());

                while (ds.getUsages().size() > schedule.daySchedules[day].getUsages().size())
                    ds.remove(ds.getUsages().last());

                day++;
            }

            Model.schedule.daySchedules = schedule.daySchedules;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
