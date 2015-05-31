package denastya.thermostat.core;

import android.app.Activity;

import java.util.ArrayList;

import denastya.thermostat.core.Watchers.BooleanWatcher;

/**
 * Created by admin on 30.05.2015.
 */
public class Model {

    private static ModeSettings currentTemp;
    private static ModeSettings settings[];
    private static ModeSettings adjustingMode;

    private static ModeUsage currentUsage;
    public static ModeUsage overridenUsage;
    private static ModeUsage nextUsage;

    private static ModeSettings editMode;
//    public static boolean needReturn;

    private static boolean overriden;
    private static boolean switchBlocked;

    private static ArrayList<BooleanWatcher> watchers;

    public static Activity activity;

    public static TimeEngine timeEngine;

    public static Schedule schedule;


    static  {
        init();
    }

    public static void init() {

        schedule = new Schedule();

        settings = new ModeSettings[3];
        settings[0] = new ModeSettings(ModeSettings.Period.DAY);
        settings[1] = new ModeSettings(ModeSettings.Period.NIGHT);
        settings[2] = new ModeSettings(ModeSettings.Period.OVERRIDE);

        settings[0].setTemperature(25.3f);
        settings[1].setTemperature(21.0f);

        currentTemp = new ModeSettings(ModeSettings.Period.ACTUAL_TEMP);
        currentTemp.setTemperature(29.3f);

        currentUsage = new ModeUsage();
        currentUsage.setSettings(settings[0]);

        nextUsage = new ModeUsage();
        nextUsage.setSettings(settings[1]);

        setEditMode(null);
//        needReturn = false;

        watchers = new ArrayList<>();

        setOverriden(false);
        setSwitchBlocked(false);

        timeEngine = new TimeEngine();

        for (int i = 0; i < 7; i++) {
            ModeUsage u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.DAY));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 0, 0, 0)));
            schedule.daySchedules[i].add(u);
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.NIGHT));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 4, 0, 0)));
            schedule.daySchedules[i].add(u);
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.DAY));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 8, 0, 0)));
            schedule.daySchedules[i].add(u);
//            if (i > 3)
//                continue;
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.NIGHT));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 11, 0, 0)));
            schedule.daySchedules[i].add(u);
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.DAY));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 16, 0, 0)));
            schedule.daySchedules[i].add(u);
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.NIGHT));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 20, 0, 0)));
            schedule.daySchedules[i].add(u);
        }
    }


    public static void onNewModeComes(ModeUsage usage) {
        if (isOverriden() && isSwitchBlocked()) {
            overridenUsage = usage;
        }
        else {
            if (overriden) {
                setOverriden(false);
            }
            setCurrentUsage(usage);
        }
    }

    public static void setNextUsage(ModeUsage usage) {
        nextUsage.copyFrom(usage);
    }

    public static ModeSettings getSettings(ModeSettings.Period period) {
        return settings[period.ordinal()];
    }

    public static ModeSettings getAdjusting() {
        return adjustingMode;
    }

    public static void setAdjusting(ModeSettings.Period period) {
        adjustingMode = getSettings(period);
    }

    public static String getAdjustingString() {
        String adjustingName = "";
        if (adjustingMode.isDay())
            adjustingName = "Day mode";
        else if (adjustingMode.isNight())
            adjustingName = "Night mode";
        else
            adjustingName = "Override";
        return adjustingName;
    }

    public static ModeSettings getCurrentTemp() {
        return currentTemp;
    }

    public static ModeUsage getCurrentUsage() {
        return currentUsage;
    }

    public static void setCurrentUsage(ModeUsage usage) {
        currentUsage.copyFrom(usage);
    }

    public static ModeUsage getNextUsage() {
        return nextUsage;
    }

    public static ModeSettings getEditMode() {
        return editMode;
    }

    public static void setEditMode(ModeSettings editMode) {
        Model.editMode = editMode;
    }

    public static boolean isOverriden() {
        return overriden;
    }

    public static void setOverriden(boolean overriden) {
        Model.overriden = overriden;

        if (overriden) {
            if (overridenUsage == null)
                overridenUsage = new ModeUsage();
            overridenUsage.copyFrom(currentUsage);
        }

        if (!overriden && overridenUsage != null)
            currentUsage.copyFrom(overridenUsage);

        for (BooleanWatcher w : watchers)
            w.onChange(overriden);
    }

    public static boolean isSwitchBlocked() {
        return switchBlocked;
    }

    public static void setSwitchBlocked(boolean switchBlocked) {
        Model.switchBlocked = switchBlocked;
    }

    public static void attachOverridenWatcher(BooleanWatcher watcher) {
        watchers.add(watcher);
        watcher.onChange(overriden);
    }
}
