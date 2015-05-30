package denastya.thermostat.core;

/**
 * Allows to get gametime.
 * Also allows to set, pause and resume it, and change timefactor.
 * Can be used in TimeShift!
 * Added daytime support.
 *
 * @author dhabensky <dhabensky@idp-crew.com>
 */
public final class GameTime {

    private double timeFactor;
    private long startSysMillis;
    private long startGameTicks;
    private boolean paused;

    private long dayDurationTicks;
    private long initialDayTicks;


    /**
     * Constructs default gametime.
     */
    public GameTime() {
        this.timeFactor = 300;
        this.paused = true;
        this.dayDurationTicks = 24 * 60 * 60 * 1000;
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
     * Returns time of gameday, value between 0 and getDayDurationTicks().
     *
     * @return time of gameday
     */
    public long getDayTimeTicks() {
        return getDayTimeTicks(getTicks());
    }

    /**
     * Returns time of gameday for given time stamp, value between 0 and getDayDurationTicks().
     *
     * @param timeStamp given time stamp
     * @return time of gameday
     */
    public long getDayTimeTicks(long timeStamp) {
        return (timeStamp + initialDayTicks) % dayDurationTicks;
    }

    /**
     * Returns time of gameday, via struct of hours and minutes.
     *
     * @return time of gameday
     */
    public GameDayTime getDayTime() {
        return getDayTime(getTicks());
    }

    /**
     * Returns time of gameday for given time stamp, via struct of hours and minutes.
     *
     * @param timeStamp given time stamp
     * @return time of gameday
     */
    public GameDayTime getDayTime(long timeStamp) {
        final long dayTime = getDayTimeTicks(timeStamp);
        final double hours = dayTime / (dayDurationTicks / 24.0);
        final byte bHours = (byte)hours;
        final byte mins = (byte)((hours - bHours) * 60);
        return new GameDayTime(bHours, mins);
    }

    /**
     * Returns time of gameday in hh:mm (or h:mm) format.
     *
     * @return time of gameday
     */
    public String getDayTimeString() {
        return getDayTimeString(getTicks());
    }

    /**
     * Returns time of gameday for given time stamp in hh:mm (or h:mm) format.
     *
     * @param timeStamp given time stamp
     * @return time of gameday
     */
    public String getDayTimeString(long timeStamp) {
        return getDayTime(timeStamp).toString();
    }



    /**
     * Returns gametime passed since last call of {@code start()} or {@code setTicks()}.
     *
     * @return gametime passed
     */
    private long timePassed() {
        return (long)((System.currentTimeMillis() - startSysMillis) * timeFactor);
    }


    /**
     * Class that holds hours and minutes of gameday.
     */
    public static final class GameDayTime {

        public final byte hours;
        public final byte mins;


        /**
         * Constructs GameDayTime with zero values.
         */
        private GameDayTime() {
            this.hours = 0;
            this.mins = 0;
        }

        /**
         * Constructs GameDayTime with given values.
         *
         * @param hours hours of gameday
         * @param mins minutes of gameday
         */
        private GameDayTime(byte hours, byte mins) {
            this.hours = hours;
            this.mins = mins;
        }


        @Override
        public String toString() {
            String mins = "0" + this.mins;
            return hours + ":" + mins.substring(mins.length() - 2);
        }

    }

}

