package denastya.thermostat.core;

import java.util.Calendar;

/**
 * Created by admin on 21.05.2015.
 */
public class Mode {

    private Calendar start;
    private Calendar end;
    private ModeSettings settings;


    public Mode() {

    }


    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public ModeSettings getSettings() {
        return settings;
    }

    public void setSettings(ModeSettings settings) {
        this.settings = settings;
    }

}
