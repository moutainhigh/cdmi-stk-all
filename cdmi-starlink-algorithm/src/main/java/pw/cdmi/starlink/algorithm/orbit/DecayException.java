package pw.cdmi.starlink.algorithm.orbit;

import java.util.Date;

import pw.cdmi.starlink.core.orbit.Julian;

public class DecayException extends PropagationException {
    /// <summary>
    /// The GMT when the satellite orbit decays.
    /// </summary>
    public Date DecayTime;

    /// <summary>
    /// The name of the satellite whose orbit decayed.
    /// </summary>
    public String SatelliteName;

    public DecayException() { }
    public DecayException(String message) {
        super(message);
    }
    public DecayException(String message, Throwable throwable){
        super(message, throwable);
    }

    public DecayException(Julian decayTime, String satelliteName)
    {
        this(decayTime.toTime(), satelliteName);
    }

    public DecayException(Date decayTime, String satelliteName)
    {
        DecayTime = decayTime;
        SatelliteName = satelliteName;
    }

}
