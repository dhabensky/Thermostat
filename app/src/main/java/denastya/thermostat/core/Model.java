package denastya.thermostat.core;

/**
 * Created by admin on 30.05.2015.
 */
public class Model {

    private static ModeSettings currentTemp;
    private static ModeSettings currentMode;
    private static ModeSettings settings[];
    private static ModeSettings adjustingMode;

    private static ModeUsage currentUsage;
    private static ModeUsage nextUsage;

    private static ModeSettings editMode;


    static  {
        int len = ModeSettings.Period.values().length;
        settings = new ModeSettings[len];
        int i = 0;
        for (ModeSettings.Period p : ModeSettings.Period.values()) {
            settings[i++] = new ModeSettings(p);
        }

        settings[0].setTemperature(25.3f);
        settings[1].setTemperature(21.0f);

        currentTemp = settings[4];
        currentTemp.setTemperature(29.3f);

        currentMode = settings[0];

        currentUsage = new ModeUsage();
        currentUsage.setSettings(currentMode);

        nextUsage = new ModeUsage();
        nextUsage.setSettings(settings[1]);

        setEditMode(null);
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

    public static ModeSettings getCurrentMode() {
        return currentMode;
    }

    public static ModeSettings getCurrentTemp() {
        return currentTemp;
    }

    public static ModeUsage getCurrentUsage() {
        return currentUsage;
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
}
