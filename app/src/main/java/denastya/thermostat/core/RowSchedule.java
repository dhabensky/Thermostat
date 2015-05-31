package denastya.thermostat.core;

import java.util.Comparator;

public class RowSchedule {

    int hours;
    int minutes;
    DayPart dayPart;
    public enum DayPart {
        AM,
        PM
    }
    ModeSettings.Period period;

    public RowSchedule(int hours, int minutes, DayPart dp, ModeSettings.Period period) {
        this.hours = hours;
        this.minutes = minutes;
        dayPart = dp;
        this.period = period;
    }

    public ModeSettings.Period getPeriod() {
        return period;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public DayPart getDayPart() {
        return dayPart;
    }

    public static Comparator<RowSchedule> getComparator() {
        return new Comparator<RowSchedule>() {
            @Override
            public int compare(RowSchedule rowSchedule, RowSchedule t1) {
                if (rowSchedule.getDayPart() == t1.getDayPart()) {
                    if (rowSchedule.getHours() == t1.getHours()) {
                        if (rowSchedule.getMinutes() > t1.getMinutes()) {
                            ModeSettings.Period tmp = rowSchedule.period;
                            rowSchedule.period = t1.period;
                            t1.period = tmp;
                            return -1;
                        } else if (rowSchedule.getMinutes() < t1.getMinutes()) {
                            return 1;
                        } else
                            return 0;
                    } else  {
                        if (rowSchedule.getHours() > t1.getHours()) {
                            ModeSettings.Period tmp = rowSchedule.period;
                            rowSchedule.period = t1.period;
                            t1.period = tmp;
                            return -1;
                        }
                        else
                            return 1;
                    }
                } else if (rowSchedule.getDayPart() == DayPart.AM)
                    return 1;
                ModeSettings.Period tmp = rowSchedule.period;
                rowSchedule.period = t1.period;
                t1.period = tmp;
                return -1;
            }
        };
    }

    @Override
    public String toString() {
        return hours + ":" + minutes + " " + (getDayPart() == DayPart.AM ? "AM":"PM");
    }
}
