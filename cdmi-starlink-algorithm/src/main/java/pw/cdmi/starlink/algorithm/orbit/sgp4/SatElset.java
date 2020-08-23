package pw.cdmi.starlink.algorithm.orbit.sgp4;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import pw.cdmi.starlink.algorithm.orbit.EciTime;
import pw.cdmi.starlink.core.orbit.Tle;

public class SatElset {
    /// <summary>
    /// The satellite name.
    /// </summary>
    public String name;

    /// <summary>
    /// Information related to the satellite's orbit.
    /// </summary>
    public Orbit orbit;


    public SatElset(Tle tle) {
        orbit = new Orbit(tle);
        this.name = orbit.SatName();
    }

    /// <summary>
    /// Standard constructor.
    /// </summary>
    /// <param name="tle">TLE data.</param>
    /// <param name="name">Optional satellite name.</param>
    public SatElset(Tle tle, String name) {
        orbit = new Orbit(tle);

        if (StringUtils.isBlank(name)) {
            this.name = orbit.SatName();
        } else {
            this.name = name;
        }
    }


    /// <summary>
    /// Returns the ECI position of the satellite.
    /// </summary>
    /// <param name="utc">The time (UTC) of position calculation.</param>
    /// <returns>The ECI location of the satellite at the given time.</returns>
    public EciTime PositionEci(Date utc) {
        return orbit.positionEci(utc);
    }

    /// <summary>
    /// Returns the ECI position of the satellite.
    /// </summary>
    /// <param name="mpe">The time of position calculation, in minutes-past-epoch.</param>
    /// <returns>The ECI location of the satellite at the given time.</returns>
    public EciTime PositionEci(double mpe) {
        return orbit.positionEci(mpe);
    }
    
    public Date getEpochTime() {
    	return orbit.getEpochTime();
    }
}
