package denastya.thermostat.core;

import android.media.audiofx.BassBoost;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import denastya.thermostat.core.Watchers.SettingsPeriodWatcher;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;
import denastya.thermostat.core.Watchers.UsageTimeWatcher;
import denastya.thermostat.ui.Watchers.TempWatcher;
import denastya.thermostat.ui.Watchers.TimeWatcher;

/**
 * Created by admin on 21.05.2015.
 */
public class ModeUsage implements Comparable<ModeUsage>, Serializable {

    public TimeEngine.WeekTime startTime;
    private ModeSettings settings;

    transient
    private SettingsTempWatcher settingsTempWatcher;

    transient
    private ArrayList<UsageTimeWatcher> watchers;
    transient
    private ArrayList<SettingsTempWatcher> tempWatchers;


    public ModeUsage() {
        this.watchers = new ArrayList<>();
        this.tempWatchers = new ArrayList<>();
        this.startTime = new TimeEngine.WeekTime();

        this.settingsTempWatcher = new SettingsTempWatcher() {

            @Override
            public void onChange(ModeSettings settings) {

                for (SettingsTempWatcher w : tempWatchers)
                    w.onChange(settings);
            }

        };
    }


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        try {
            ois.defaultReadObject();
            this.watchers = new ArrayList<>();
            this.tempWatchers = new ArrayList<>();

            this.settingsTempWatcher = new SettingsTempWatcher() {

                @Override
                public void onChange(ModeSettings settings) {

                    for (SettingsTempWatcher w : tempWatchers)
                        w.onChange(settings);
                }

            };

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TimeEngine.WeekTime getStart() {
        return startTime;
    }

    public void setStartTime(TimeEngine.WeekTime start) {
        this.startTime = start;

        for (UsageTimeWatcher w : watchers)
            w.onChange(this);
    }

    public String getStartString() {
        String[] days = {
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat",
                "Sun",
                "Mon",
        };
        String res = startTime.hours % 12 + ":";
        //res = (res.equals("0:") ? "12:" : res);
        res = days[(startTime.days + 4) % 7] + "  "  + res;
        String temp = ("0" + startTime.mins);
        temp = temp.substring(temp.length() - 2);
        res += temp;
        temp = (startTime.hours < 12 ? "am" : "pm");
        res += " " + temp;
        return res;
    }

    public ModeSettings getSettings() {
        return settings;
    }

    public void setSettings(ModeSettings settings) {

        if (this.settings != null)
            this.settings.detachWatcher(this.settingsTempWatcher);
        this.settings = settings;
        settings.attachWatcher(this.settingsTempWatcher);

        for (SettingsTempWatcher w : tempWatchers)
            w.onChange(settings);
    }

    public void attachWatcher(UsageTimeWatcher w) {
        this.watchers.add(w);
        w.onChange(this);
    }

    public void attachWatcher(SettingsTempWatcher w) {
        for (SettingsTempWatcher stw : tempWatchers) {
            if (stw.equals(w)) {
                return;
            }
        }
        this.tempWatchers.add(w);
        w.onChange(this.settings);
    }

    public void detachWatcher(UsageTimeWatcher w) {
        this.watchers.remove(w);
    }

    public void detachWatcher(SettingsTempWatcher w) {
        this.tempWatchers.remove(w);
    }

    public void copyFrom(ModeUsage other) {
        setSettings(other.settings);
        setStartTime(other.startTime);
    }

    @Override
    public int compareTo(ModeUsage another) {
        return startTime.compareTo(another.startTime);
    }
}
