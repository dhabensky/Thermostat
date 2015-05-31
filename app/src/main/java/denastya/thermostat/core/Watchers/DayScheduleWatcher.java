package denastya.thermostat.core.Watchers;

import denastya.thermostat.core.DaySchedule;

/**
 * Created by admin on 31.05.2015.
 */
public interface DayScheduleWatcher {

    void onChange(DaySchedule schedule);

}
