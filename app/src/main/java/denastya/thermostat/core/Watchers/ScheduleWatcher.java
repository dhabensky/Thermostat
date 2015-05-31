package denastya.thermostat.core.Watchers;

import denastya.thermostat.core.DaySchedule;
import denastya.thermostat.core.Schedule;

/**
 * Created by admin on 31.05.2015.
 */
public interface ScheduleWatcher {

    void onChange(Schedule schedule, int dayIndex);

}
