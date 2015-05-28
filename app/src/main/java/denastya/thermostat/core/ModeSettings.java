package denastya.thermostat.core;

/**
 * Created by admin on 21.05.2015.
 */
public class ModeSettings {

    private float  temperature;
    private Period period;


    public ModeSettings(Period period) {
        this.period = period;
    }


    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }

    public boolean isDay() {
        return this.period == Period.DAY;
    }

    public boolean isNight() {
        return this.period == Period.NIGHT;
    }

    public boolean isTemporaryOverriden() {
        return this.period == Period.TEMP_OVERRIDE;
    }

    public boolean isPermanentlyOverriden() {
        return this.period == Period.PERM_OVERRIDE;
    }


    public enum Period {
        DAY,
        NIGHT,
        TEMP_OVERRIDE,
        PERM_OVERRIDE
    }

}
