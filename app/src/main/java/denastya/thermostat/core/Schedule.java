package denastya.thermostat.core;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

import denastya.thermostat.core.Watchers.DayScheduleWatcher;
import denastya.thermostat.core.Watchers.ScheduleWatcher;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;
import denastya.thermostat.core.Watchers.UsageTimeWatcher;

/**
 * Created by admin on 31.05.2015.
 */
public class Schedule {

    public DaySchedule[] daySchedules = new DaySchedule[7];

    private int curDayInd;
    private ModeUsage curMode;
    private ModeUsage nextMode;

    private ArrayList<ScheduleWatcher> watchers = new ArrayList<>();


    public Schedule() {
        curMode = null;
        nextMode = null;
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


    public void switchMode(int day, ModeUsage curTime) {

        ModeUsage oldCur = curMode;
        ModeUsage oldNext = nextMode;

        day %= 7;
        curDayInd = day;

        ModeUsage cur = null;
        ModeUsage next = null;
        for (ModeUsage usage : daySchedules[day].getUsages()) {
            if (curTime.compareTo(usage) < 0) {
                next = usage;
                break;
            }
            cur = usage;
        }

//        if (cur.startTime.hours == 12) {
//            Model.getCurrentUsage();
//        }

        if (next == null) {
            int newDay = (day + 1) % 7;
            while (newDay != day) {
                if (!daySchedules[newDay].getUsages().isEmpty()) {
                    next = daySchedules[newDay].getUsages().first();
                    break;
                }
                newDay = (newDay + 1) % 7;
            }
            if (newDay == day) {
                Log.d("RRR", "PIZDEC");
                throw new NoSuchElementException("AAAAAAAAA((((");
            }
        }



        if (cur != oldCur) {
            //Log.d("RRR", cur.getStart().hours + "");
            Model.onNewModeComes(cur);
        }

        if (next != oldNext) {
            Model.setNextUsage(next);
        }

        curMode = cur;
        nextMode = next;
    }

    private void scheduleChanged(DaySchedule schedule, int index) {

        Log.d("RRR", "sched " + index + " changed. how has " + schedule.getUsages().size() + " items");
        for (ScheduleWatcher w : watchers)
            w.onChange(this, index);
    }

    public void attachWatcher(ScheduleWatcher w) {
        this.watchers.add(w);
    }

    public void detachWatcher(ScheduleWatcher w) {
        this.watchers.remove(w);
    }

    public void removeCurrent() {
        if (curMode != null) {
            daySchedules[curDayInd].remove(curMode);
            curMode = null;
        }
    }

    public void removeNext() {
        if (nextMode != null) {
            daySchedules[curDayInd].remove(nextMode);
            nextMode = null;
        }
    }



}
