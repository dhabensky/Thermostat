package denastya.thermostat.core;

import java.util.ArrayList;
import java.util.Calendar;

import denastya.thermostat.core.Watchers.SettingsPeriodWatcher;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;
import denastya.thermostat.core.Watchers.UsageTimeWatcher;

/**
 * Created by admin on 21.05.2015.
 */
public class ModeUsage {

    private Calendar start;
    private Calendar end;
    private ModeSettings settings;

    private SettingsTempWatcher settingsTempWatcher;

    private ArrayList<UsageTimeWatcher> watchers;
    private ArrayList<SettingsTempWatcher> tempWatchers;


    public ModeUsage() {
        this.watchers = new ArrayList<>();
        this.tempWatchers = new ArrayList<>();
        this.start = Calendar.getInstance();
        this.end = Calendar.getInstance();

        this.settingsTempWatcher = new SettingsTempWatcher() {

            @Override
            public void onChange(ModeSettings settings) {

                for (SettingsTempWatcher w : tempWatchers)
                    w.onChange(settings);
            }

        };
    }


    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public String getEndString() {

        String res = end.get(Calendar.HOUR) + ":";
        String temp = ("0" + end.get(Calendar.MINUTE));
        temp = temp.substring(temp.length() - 2);
        res += temp;
        temp = (end.get(Calendar.AM_PM) == 0 ? "am" : "pm");
        res += " " + temp;
        return res;
    }

    public void setEnd(Calendar end) {

        for (UsageTimeWatcher w : watchers)
            w.onChange(this);

        this.end = end;
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
        setStart(other.start);
        setEnd(other.end);
    }

}
