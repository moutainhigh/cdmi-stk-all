package pw.cdmi.starlink.algorithm.orbit;

import pw.cdmi.starlink.core.orbit.Eci;
import pw.cdmi.starlink.core.orbit.Geo;
import pw.cdmi.starlink.core.orbit.Julian;

public final class GeoTime extends Geo {
    /// <summary>
    /// The time associated with the coordinates.
    /// </summary>
    public Julian date;


    /// <summary>
    /// Standard constructor.
    /// </summary>
    /// <param name="radLat">Latitude, in radians. Negative values indicate
    /// latitude south.</param>
    /// <param name="radLon">Longitude, in radians. Negative value indicate longitude
    /// west.</param>
    /// <param name="kmAlt">Altitude above the ellipsoid model, in kilometers.</param>
    /// <param name="date">The time associated with the coordinates.</param>
    public GeoTime(double radLat, double radLon, double kmAlt, Julian date)
    {
        super(radLat, radLon, kmAlt);
        this.date = date;
    }

    /// <summary>
    /// Constructor accepting Geo and Julian objects.
    /// </summary>
    /// <param name="geo">The Geo object.</param>
    /// <param name="date">The Julian date.</param>
    public GeoTime(Geo geo, Julian date)
    {
        super(geo);
        this.date = date;
    }

    /// <summary>
    /// Creates a new instance of the class from ECI-time information.
    /// </summary>
    /// <param name="eci">The ECI-time coordinate pair.</param>
    /// <param name="ellipsoid">The earth ellipsoid model.</param>
    public GeoTime(EciTime eci)
    {
        super(eci, eci.Date);
        date = eci.Date;
    }

    /// <summary>
    /// Creates a new instance of the class from ECI coordinates.
    /// </summary>
    /// <param name="eci">The ECI coordinates.</param>
    /// <param name="date">The Julian date.</param>
    public GeoTime(Eci eci, Julian date)
    {
        super(eci, date);
        this.date = date;
    }

}
