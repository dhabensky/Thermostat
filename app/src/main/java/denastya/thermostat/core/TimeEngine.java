package denastya.thermostat.core;

import java.util.Calendar;

/**
 * Allows to get gametime.
 * Also allows to set, pause and resume it, and change timefactor.
 * Can be used in TimeShift!
 * Added daytime support.
 *
 * @author dhabensky <dhabensky@idp-crew.com>
 */
public final class TimeEngine {

    private double timeFactor;
    private long startSysMillis;
    private long startGameTicks;
    private boolean paused;


    /**
     * Constructs default gametime.
     */
    public TimeEngine() {
        this.timeFactor = 300;
        this.paused = true;
    }


    // basic gametime methods, dealing only with ticks

    /**
     * Starts the time flow in the game.
     */
    public void start() {
        if (paused) {
            startSysMillis = System.currentTimeMillis();
            paused = false;
        }
    }

    /**
     * Pauses the time flow in the game.
     */
    public void pause() {
        if (!paused) {
            startGameTicks += timePassed();
            paused = true;
        }
    }

    /**
     * Returns whether game time is paused or not.
     *
     * @return true if time is paused, otherwise false
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the timefactor.
     *
     * @param timeFactor new timefactor to set
     */
    public void setTimeFactor(double timeFactor) {
        if (paused) {
            this.timeFactor = timeFactor;
        }
        else {
            pause();
            this.timeFactor = timeFactor;
            start();
        }
    }

    /**
     * Returns current timefactor.
     *
     * @return current timefactor
     */
    public double getTimeFactor() {
        return timeFactor;
    }

    /**
     * Sets the current gametime.
     * Causes no effect on day start time, thus, day time will change.
     *
     * @param curGameTime Current gametime
     */
    public void setTicks(long curGameTime) {
        startSysMillis = System.currentTimeMillis();
        startGameTicks = curGameTime;
    }

    public void setTicks(int days, int hours, int mins, int secs) {
        long res = days * 24;
        res = (res + hours) * 60;
        res = (res + mins) * 60;
        res = (res + secs) * 1000;
        setTicks(res);
    }

    public static long convert(int days, int hours, int mins, int secs) {
        long res = days * 24;
        res = (res + hours) * 60;
        res = (res + mins) * 60;
        res = (res + secs) * 1000;
        return res;
    }

    public WeekTime getWeekTime(long timeStamp)  {
        timeStamp /= 1000;
        byte secs = (byte)(timeStamp % 60);
        timeStamp /= 60;
        byte mins = (byte)(timeStamp % 60);
        timeStamp /= 60;
        byte hours = (byte)(timeStamp % 24);
        timeStamp /= 24;
        return new WeekTime((byte)timeStamp, hours, mins, secs);
    }

    public WeekTime getWeekTime() {
        return getWeekTime(getTicks());
    }

    /**
     * Returns current gametime.
     *
     * @return current gametime
     */
    public long getTicks() {
        long result = startGameTicks;
        if (!paused) {
            result += timePassed();
        }
        return result;
    }

    /**
     * Returns gametime passed since last call of {@code start()} or {@code setTicks()}.
     *
     * @return gametime passed
     */
    private long timePassed() {
        return (long)((System.currentTimeMillis() - startSysMillis) * timeFactor);
    }



    public static final class WeekTime implements Comparable<WeekTime> {

        public final byte days;
        public final byte hours;
        public final byte mins;
        public final byte secs;


        public WeekTime() {
            this.days = 0;
            this.hours = 0;
            this.mins = 0;
            this.secs = 0;
        }


        private WeekTime(byte days, byte hours, byte mins, byte secs) {
            this.days = days;
            this.hours = hours;
            this.mins = mins;
            this.secs = secs;
        }

        private long toTicks() {
            long res = hours * 60;
            res = (res + mins) * 60;
            res = (res + secs) * 1000;
            return res;
        }

        @Override
        public String toString() {
            String mins = "0" + this.mins;
            return hours + ":" + mins.substring(mins.length() - 2);
        }

        @Override
        public int compareTo(WeekTime another) {
            long l1 = toTicks();
            long l2 = another.toTicks();
            if (l1 == l2) return 0;
            else return (l1 < l2 ? -1 : 1);
            //return Long.compare(toTicks(), another.toTicks());
        }
    }

}

