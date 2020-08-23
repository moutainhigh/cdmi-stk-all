package pw.cdmi.starlink.algorithm.orbit;

import java.util.Date;

import pw.cdmi.starlink.core.orbit.Geo;
import pw.cdmi.starlink.core.orbit.Globals;
import pw.cdmi.starlink.core.orbit.Julian;
import pw.cdmi.starlink.core.orbit.Vector;

public final class Site {
    /// <summary>
    /// The name of the location.
    /// </summary>
    public String name;
    /// <summary>
    /// The contained geodetic coordinates.
    /// </summary>
    public Geo geo;

    /// <summary>
    /// Latitude, in radians. A negative value indicates latitude south.
    /// </summary>
    public double getLatitudeRad() {
        return geo.getLatitudeRad(); }

    /// <summary>
    /// Longitude, in radians. A negative value indicates longitude west.
    /// </summary>
    public double getLongitudeRad() {
        return geo.getLongitudeRad(); }

    /// <summary>
    /// Latitude, in degrees. A negative value indicates latitude south.
    /// </summary>
    public double latitudeDeg() {
        return geo.LatitudeDeg(); }

    /// <summary>
    /// Longitude, in degrees. A negative value indicates longitude west.
    /// </summary>
    public double longitudeDeg() { return geo.LongitudeDeg(); }

    /// <summary>
    /// The altitude of the site above the ellipsoid model, in kilometers
    /// </summary>
    public double altitude() { return geo.getAltitude(); }

    /// <summary>
    /// Standard constructor.
    /// </summary>
    /// <param name="degLat">Latitude in degrees (negative south).</param>
    /// <param name="degLon">Longitude in degrees (negative west).</param>
    /// <param name="kmAlt">Altitude in kilometers.</param>
    /// <param name="model">The earth ellipsoid model.</param>
    public Site(double degLat, double degLon, double kmAlt, String name)
    {
        geo = new Geo(Globals.toRadians(degLat),
                Globals.toRadians(degLon),
                kmAlt);
        this.name = name;
    }

    /// <summary>
    /// Create a Site object from Geo object.
    /// </summary>
    /// <param name="geo">The Geo object.</param>
    public Site(Geo geo)
    {
        geo = new Geo(geo);
    }

    /// <summary>
    /// Calculates the ECI coordinates of the site.
    /// </summary>
    /// <param name="time">Time of position calculation.</param>
    /// <returns>The site's ECI coordinates at the given time.</returns>
    public EciTime GetPosition(Julian time)
    {
        return new EciTime(geo, time);
    }

    /// <summary>
    /// Calculates the ECI coordinates of the site.
    /// </summary>
    /// <param name="date">Time of position calculation.</param>
    /// <returns>The site's ECI coordinates at the given time.</returns>
    public EciTime PositionEci(Julian time)
    {
        return new EciTime(geo, time);
    }

    /// <summary>
    /// Calculates the ECI coordinates of the site.
    /// </summary>
    /// <param name="utc">Time of position calculation.</param>
    /// <returns>The site's ECI coordinates at the given time.</returns>
    public EciTime PositionEci(Date utc) throws IllegalAccessException
    {
        return new EciTime(geo, new Julian(utc));
    }

    /// <summary>
    /// Returns the topo-centric (azimuth, elevation, etc.) coordinates for
    /// a target object described by the given ECI coordinates.
    /// </summary>
    /// <param name="eci">The ECI coordinates of the target object.</param>
    /// <returns>The look angle to the target object.</returns>
    public TopoTime GetLookAngle(EciTime eci) throws IllegalAccessException
    {
        // Calculate the ECI coordinates for this Site object at the time
        // of interest.
        Julian  date     = eci.Date;
        EciTime eciSite  = PositionEci(date);
        Vector vecRgRate = new Vector(eci.velocity.X - eciSite.velocity.X,
                eci.velocity.Y - eciSite.velocity.Y,
                eci.velocity.Z - eciSite.velocity.Z);

        double x = eci.position.X - eciSite.position.X;
        double y = eci.position.Y - eciSite.position.Y;
        double z = eci.position.Z - eciSite.position.Z;
        double w = Math.sqrt(Globals.sqr(x) + Globals.sqr(y) + Globals.sqr(z));

        Vector vecRange = new Vector(x, y, z, w);

        // The site's Local Mean Sidereal Time at the time of interest.
        double theta = date.toLmst(geo.getLongitudeRad());

        double sin_lat   = Math.sin(geo.getLatitudeRad());
        double cos_lat   = Math.cos(geo.getLatitudeRad());
        double sin_theta = Math.sin(theta);
        double cos_theta = Math.cos(theta);

        double top_s = sin_lat * cos_theta * vecRange.X +
                sin_lat * sin_theta * vecRange.Y -
                cos_lat * vecRange.Z;
        double top_e = -sin_theta * vecRange.X +
                cos_theta * vecRange.Y;
        double top_z = cos_lat * cos_theta * vecRange.X +
                cos_lat * sin_theta * vecRange.Y +
                sin_lat * vecRange.Z;
        double az    = Math.atan(-top_e / top_s);

        if (top_s > 0.0)
        {
            az += Globals.PI;
        }

        if (az < 0.0)
        {
            az += 2.0 * Globals.PI;
        }

        double el   = Math.asin(top_z / vecRange.W);
        double rate = (vecRange.X * vecRgRate.X +
                vecRange.Y * vecRgRate.Y +
                vecRange.Z * vecRgRate.Z) / vecRange.W;

        TopoTime topo = new TopoTime(az,         // azimuth, radians
                el,         // elevation, radians
                vecRange.W, // range, km
                rate,       // rate, km / sec
                eci.Date);

        // Elevation correction for atmospheric refraction.
        // Reference:  Astronomical Algorithms by Jean Meeus, pp. 101-104
        // Note:  Correction is meaningless when apparent elevation is below horizon
        topo.elevationRad += Globals.toRadians((1.02 /
                Math.tan(Globals.toRadians(Globals.toDegrees(el) + 10.3 /
                        (Globals.toDegrees(el) + 5.11)))) / 60.0);
        if (topo.elevationRad < 0.0)
        {
            topo.elevationRad = el;    // Reset to true elevation
        }

        if (topo.elevationRad > (Math.PI / 2.0))
        {
            topo.elevationRad = (Math.PI / 2.0);
        }

        return topo;
    }

    /// <summary>
    /// Converts to a string representation of the form "120.00N 090.00W 500m".
    /// </summary>
    /// <returns>The formatted string.</returns>
    @Override
    public String toString()
    {
        return geo.toString();
    }
}
