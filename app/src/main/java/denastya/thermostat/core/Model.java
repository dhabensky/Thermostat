package denastya.thermostat.core;

import android.app.Activity;

import java.util.ArrayList;

import denastya.thermostat.HostActivity;
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

    private static boolean overriden;
    private static boolean switchBlocked;

    private static ArrayList<BooleanWatcher> watchers;

    public static HostActivity activity;

    public static TimeEngine timeEngine;

    public static Schedule schedule;


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

        watchers = new ArrayList<>();

        setOverriden(false);
        setSwitchBlocked(false);

        timeEngine = new TimeEngine();

        for (int i = 0; i < 7; i++) {
            ModeUsage u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.NIGHT));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 0, 0, 0)));
            schedule.daySchedules[i].add(u);
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.DAY));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 12, 0, 0)));
            schedule.daySchedules[i].add(u);
            u = new ModeUsage();
            u.setSettings(getSettings(ModeSettings.Period.NIGHT));
            u.setStartTime(timeEngine.getWeekTime(
                    TimeEngine.convert(i, 22, 0, 0)));
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

        boolean wasOverriden = Model.isOverriden();
        Model.overriden = overriden;

        if (wasOverriden && !overriden) {
            currentUsage.copyFrom(overridenUsage);
            overridenUsage = null;
        }

        if (overriden) {
            if (overridenUsage == null)
                overridenUsage = new ModeUsage();
            overridenUsage.copyFrom(currentUsage);
        }
//        else
//            overridenUsage = null;

        if (!overriden && overridenUsage != null)
            currentUsage.copyFrom(Model.schedule.curMode);
//            currentUsage.copyFrom(overridenUsage);

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
