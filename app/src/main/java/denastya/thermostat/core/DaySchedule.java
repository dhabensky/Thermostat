package denastya.thermostat.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import denastya.thermostat.core.Watchers.DayScheduleWatcher;
import denastya.thermostat.core.Watchers.SettingsPeriodWatcher;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;

/**
 * Created by admin on 31.05.2015.
 */
public class DaySchedule {

    private TreeSet<ModeUsage> usages = new TreeSet<>();

    private ArrayList<DayScheduleWatcher> watchers = new ArrayList<>();


    public void add(ModeUsage usage) {
        usages.add(usage);

        for (DayScheduleWatcher w : watchers)
            w.onChange(this);
    }

    public void remove(ModeUsage usage) {
        usages.remove(usage);

        for (DayScheduleWatcher w : watchers)
            w.onChange(this);
    }

    public TreeSet<ModeUsage> getUsages() {
        return usages;
    }

    public void attachWatcher(DayScheduleWatcher w) {
        this.watchers.add(w);
        w.onChange(this);
    }

    public void detachWatcher(DayScheduleWatcher w) {
        this.watchers.remove(w);
    }


}
