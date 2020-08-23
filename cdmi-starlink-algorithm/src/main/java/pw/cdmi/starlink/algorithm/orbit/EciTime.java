package pw.cdmi.starlink.algorithm.orbit;

import pw.cdmi.starlink.core.orbit.Eci;
import pw.cdmi.starlink.core.orbit.Geo;
import pw.cdmi.starlink.core.orbit.Julian;
import pw.cdmi.starlink.core.orbit.Vector;

public class EciTime extends  Eci {
    /// <summary>
    /// The time associated with the ECI coordinates.
    /// </summary>
    public Julian Date;

    /// <summary>
    /// Creates an instance of the class with the given position, velocity, and time.
    /// </summary>
    /// <param name="pos">The position vector.</param>
    /// <param name="vel">The velocity vector.</param>
    /// <param name="date">The time associated with the position.</param>
    public EciTime(Vector pos, Vector vel, Julian date)
    {
        super(pos, vel);
        Date = date;
    }

    /// <summary>
    /// Creates a new instance of the class from ECI-time coordinates.
    /// </summary>
    /// <param name="eci">The ECI coordinates.</param>
    /// <param name="date">The time associated with the ECI coordinates.</param>
    public EciTime(Eci eci, Julian date)
    {
        this(eci.position, eci.velocity, date);
    }

    /// <summary>
    /// Creates a new instance of the class from geodetic coordinates.
    /// </summary>
    /// <param name="geo">The geodetic coordinates.</param>
    /// <param name="date">The time associated with the ECI coordinates.</param>
    public EciTime(Geo geo, Julian date)
    {
        super(geo, date);
        Date = date;
    }

    /// <summary>
    /// Creates a new instance of the class from geodetic-time coordinates.
    /// </summary>
    /// <param name="geo">The geodetic-time coordinates.</param>
    public EciTime(GeoTime geo)
    {
        this(geo, geo.date);
    }
}
