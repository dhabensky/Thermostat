package denastya.thermostat.core;

/**
 * Created by admin on 29.05.2015.
 */
public class ModeManager {

    private static ModeSettings settings[];
    private static ModeManager instance;


    public ModeManager() {
        int len = ModeSettings.Period.values().length;
        settings = new ModeSettings[len];
        int i = 0;
        for (ModeSettings.Period p : ModeSettings.Period.values()) {
            settings[i++] = new ModeSettings(p);
        }

        settings[0].setTemperature(25.3f);
        settings[1].setTemperature(21.0f);
    }

    public static ModeSettings getSettings(ModeSettings.Period period) {
        return settings[period.ordinal()];
    }

}
