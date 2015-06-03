package denastya.thermostat.core;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
public class Schedule implements Serializable {

    public DaySchedule[] daySchedules = new DaySchedule[7];

    transient
    private int curDayInd;

    transient
    public ModeUsage curMode;

    transient
    private ModeUsage nextMode;

    transient
    private ArrayList<ScheduleWatcher> watchers = new ArrayList<>();


    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        watchers = new ArrayList<>();
    }

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
        Log.d("sched", "cur: day = " + cur.getStart().days + "; hour = " + cur.getStart().hours);

        if (cur == null) {
            Log.d("sched", "cur == null, day = " + day);
            cur = daySchedules[(day + 6) % 7].getUsages().last();
            Log.d("sched", "cur != null, day = " + cur.getStart().days + "; hour = " + cur.getStart().hours);
        }

        if (next == null) {
            Log.d("sched", "next == null, day = " + day);
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
            Log.d("sched", "next != null, day = " + newDay + "; hour = " + next.getStart().hours);
        }


//        Log.d("sched", cur.getStart().days + " " + cur.getStart().hours);
//        Log.d("sched", next.getStart().days + " " + next.getStart().hours);


        if (cur != oldCur) {
            Log.d("sched", "switch cur");
            Log.d("sched", "cur:   , day = " + day + "; hour = " + cur.getStart().hours);
            if (oldCur != null)
                Log.d("sched", "oldCur:, day = " + day + "; hour = " + oldCur.getStart().hours);
            else
                Log.d("sched", "oldCur:, null");
            //Log.d("RRR", cur.getStart().hours + "");
            Model.onNewModeComes(cur);
        }

        if (next != oldNext) {
            Log.d("sched", "switch next");
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
