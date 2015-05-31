package denastya.thermostat.core;

import java.util.ArrayList;
import java.util.Calendar;

import denastya.thermostat.core.Watchers.SettingsPeriodWatcher;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;
import denastya.thermostat.core.Watchers.UsageTimeWatcher;
import denastya.thermostat.ui.Watchers.TimeWatcher;

/**
 * Created by admin on 21.05.2015.
 */
public class ModeUsage implements Comparable<ModeUsage> {

    private Calendar start;
    private Calendar end;
    public TimeEngine.WeekTime startTime;
    private ModeSettings settings;

    private SettingsTempWatcher settingsTempWatcher;

    private ArrayList<UsageTimeWatcher> watchers;
    private ArrayList<SettingsTempWatcher> tempWatchers;


    public ModeUsage() {
        this.watchers = new ArrayList<>();
        this.tempWatchers = new ArrayList<>();
        this.start = Calendar.getInstance();
        this.startTime = new TimeEngine.WeekTime();
        this.end = Calendar.getInstance();

        this.settingsTempWatcher = new SettingsTempWatcher() {

            @Override
            public void onChange(ModeSettings settings) {

                for (SettingsTempWatcher w : tempWatchers)
                    w.onChange(settings);
            }

        };
    }


    public TimeEngine.WeekTime getStart() {
        return startTime;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public void setStartTime(TimeEngine.WeekTime start) {
        this.startTime = start;

        for (UsageTimeWatcher w : watchers)
            w.onChange(this);
    }

    public Calendar getEnd() {
        return end;
    }

    public String getEndString() {
        String res = end.get(Calendar.HOUR) + ":";
        res = (res.equals("0:") ? "12:" : res);
        String temp = ("0" + end.get(Calendar.MINUTE));
        temp = temp.substring(temp.length() - 2);
        res += temp;
        temp = (end.get(Calendar.AM_PM) == 0 ? "am" : "pm");
        res += " " + temp;
        return res;
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
        res = (res.equals("0:") ? "12:" : res);
        res = days[startTime.days] + "  "  + res;
        String temp = ("0" + startTime.mins);
        temp = temp.substring(temp.length() - 2);
        res += temp;
        temp = (startTime.hours < 12 ? "am" : "pm");
        res += " " + temp;
        return res;
    }

    public void setEnd(Calendar end) {

        this.end = end;

        for (UsageTimeWatcher w : watchers)
            w.onChange(this);

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
        setStartTime(other.startTime);
    }

    @Override
    public int compareTo(ModeUsage another) {
        return startTime.compareTo(another.startTime);
    }
}
