package denastya.thermostat.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

import denastya.thermostat.core.Watchers.DayScheduleWatcher;
import denastya.thermostat.core.Watchers.ScheduleWatcher;

/**
 * Created by admin on 31.05.2015.
 */
public class Schedule {

    public DaySchedule[] daySchedules = new DaySchedule[7];

    private int curDayInd;
    private Iterator<ModeUsage> iter;
    private ModeUsage curMode;
    private ModeUsage nextMode;
    boolean switchNeeded = false;

    //private ArrayList<DayScheduleWatcher> dayWatchers = new ArrayList<>();

    private ArrayList<ScheduleWatcher> watchers = new ArrayList<>();


    public Schedule() {
        curMode = null;
        nextMode = null;
        iter = null;
        curDayInd = -1;

        for (int i = 0; i < 7; i++)
            daySchedules[i] = new DaySchedule();

        for (int i = 0; i < 7; i++) {
            final int j = i;
            DayScheduleWatcher w = new DayScheduleWatcher() {

                @Override
                public void onChange(DaySchedule schedule) {
                    scheduleChanged(schedule, j);
                }
            };
            daySchedules[i].attachWatcher(w);
        }

    }


//    public void init(int startDay, ModeUsage startTime) {
//        curDayInd = startDay;
//    }

    private void setDay(int day) {
        day %= 7;
        if (day != curDayInd) {
            switchNeeded = true;
            curDayInd = day;
            iter = daySchedules[curDayInd].getUsages().iterator();
            if (iter.hasNext())
                nextMode = iter.next();
            else
                nextMode = null;
            curMode = null;
        }
    }

    public void switchMode(int day, ModeUsage curTime) {

        switchNeeded = false;
        ModeUsage old = curMode;

        setDay(day);
        if (nextMode == null)
            return;

        while (iter.hasNext() && curTime.compareTo(nextMode) >= 0) {
            switchNeeded = true;
            curMode = nextMode;
            nextMode = iter.next();
        }

        if (curTime.compareTo(nextMode) >= 0) {
            curMode = nextMode;
            if (old != curMode) {
                Model.onNewModeComes(curMode);
                Model.setNextUsage(findNextMode());
            }
            return;
        }

        if (switchNeeded) {
            Model.onNewModeComes(curMode);
            Model.setNextUsage(nextMode);
        }
    }

    private ModeUsage findNextMode() {

        int day = curDayInd;
        while (true) {
            day = (day + 1) % 7;
            Iterator<ModeUsage> it = daySchedules[day].getUsages().iterator();
            if (it.hasNext())
                return it.next();
            if (day == curDayInd)
                throw new NoSuchElementException("PIZDEC");
        }
    }

    private void scheduleChanged(DaySchedule schedule, int index) {

        for (ScheduleWatcher w : watchers)
            w.onChange(this, index);
    }



}
