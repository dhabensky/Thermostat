package denastya.thermostat.core;

import java.util.ArrayList;

import denastya.thermostat.core.Watchers.SettingsPeriodWatcher;
import denastya.thermostat.core.Watchers.SettingsTempWatcher;

/**
 * Created by admin on 21.05.2015.
 */
public class ModeSettings {

    private float  temperature;
    private Period period;

    private ArrayList<SettingsTempWatcher> tempWatchers;
    private ArrayList<SettingsPeriodWatcher> periodWatchers;


    public ModeSettings(Period period) {
        this.period = period;
        this.tempWatchers = new ArrayList<SettingsTempWatcher>();
        this.periodWatchers = new ArrayList<SettingsPeriodWatcher>();
    }


    public void setTemperature(float temperature) {
        this.temperature = temperature;

        for (SettingsTempWatcher w : tempWatchers)
            w.onChange(this);
    }

    public float getTemperature() {
        return temperature;
    }

    public int getTemperatureInt() {
        return (int)temperature;
    }

    public int getTemperatureFrac() {
        return Math.round((temperature - (int)temperature) * 10);
    }

    public String getTemperatureString() {
        return String.format("%.1f", temperature).replace(",", ".");
    }

    public Period getPeriod() {
        return this.period;
    }

    public void setPeriod(Period period) {
        this.period = period;

        for (SettingsPeriodWatcher w : periodWatchers)
            w.onChange(this);
    }

    public boolean isDay() {
        return this.period == Period.DAY;
    }

    public boolean isNight() {
        return this.period == Period.NIGHT;
    }

    public boolean isOverriden() {
        return this.period == Period.OVERRIDE;
    }

    public void attachWatcher(SettingsTempWatcher w) {
        this.tempWatchers.add(w);
        w.onChange(this);
    }

    public void attachWatcher(SettingsPeriodWatcher w) {
        this.periodWatchers.add(w);
        w.onChange(this);
    }

    public void detachWatcher(SettingsTempWatcher w) {
        this.tempWatchers.remove(w);
    }

    public void detachWatcher(SettingsPeriodWatcher w) {
        this.periodWatchers.remove(w);
    }


    public enum Period {
        DAY,
        NIGHT,
        OVERRIDE,
        ACTUAL_TEMP
    }

}
