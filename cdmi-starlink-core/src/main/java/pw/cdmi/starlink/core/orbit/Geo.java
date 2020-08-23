package pw.cdmi.starlink.core.orbit;

public class Geo {
    /// <summary>
    /// Latitude, in radians. A negative value indicates latitude south.
    /// </summary>
    private final double latitudeRad;

    /// <summary>
    /// Longitude, in radians. A negative value indicates longitude west.
    /// </summary>
    private final double longitudeRad;

    /// <summary>
    /// Altitude, in kilometers, above the ellipsoid model.
    /// </summary>
    private final double altitude;

    /// <summary>
    /// Latitude, in degrees. A negative value indicates latitude south.
    /// </summary>
    public double LatitudeDeg() {
        return Globals.toDegrees(latitudeRad); }

    /// <summary>
    /// Longitude, in degrees. A negative value indicates longitude west.
    /// </summary>
    public double LongitudeDeg() { return Globals.toDegrees(longitudeRad); }


    /// <summary>
    /// Creates a Geo object from a source Geo object.
    /// </summary>
    /// <param name="geo">The source Geo object.</param>
    public Geo(Geo geo)
    {
        latitudeRad = geo.latitudeRad;
        longitudeRad = geo.longitudeRad;
        altitude = geo.altitude;
    }

    /// <summary>
    /// Creates a new instance of the class with the given components.
    /// </summary>
    /// <param name="radLat">Latitude, in radians. Negative values indicate
    /// latitude south.</param>
    /// <param name="radLon">Longitude, in radians. Negative value indicate longitude
    /// west.</param>
    /// <param name="kmAlt">Altitude above the ellipsoid model, in kilometers.</param>
    public Geo(double radLat, double radLon, double kmAlt)
    {
        latitudeRad = radLat;
        longitudeRad = radLon;
        altitude = kmAlt;
    }

    /// <summary>
    /// Creates a new instance of the class given ECI coordinates.
    /// </summary>
    /// <param name="eci">The ECI coordinates.</param>
    /// <param name="date">The Julian date.</param>
    public Geo(Eci eci, Julian date)
    {
        this(eci.position,
            (Globals.acTan(eci.position.Y, eci.position.X) - date.toGmst()) % Globals.TWO_PI);
    }

    /// <summary>
    /// Creates a new instance of the class given XYZ coordinates.
    /// </summary>
    private Geo(Vector pos, double theta)
    {
        theta = theta % Globals.TWO_PI;

        if (theta < 0.0)
        {
            // "wrap" negative modulo
            theta += Globals.TWO_PI;
        }

        double r = Math.sqrt(Globals.sqr(pos.X) + Globals.sqr(pos.Y));
        double e2 = Globals.F * (2.0 - Globals.F);
        double lat = Globals.acTan(pos.Z, r);

        final double DELTA = 1.0e-07;
        double phi;
        double c;

        do
        {
            phi = lat;
            c = 1.0 / Math.sqrt(1.0 - e2 * Globals.sqr(Math.sin(phi)));
            lat = Globals.acTan(pos.Z + Globals.Xkmper * c * e2 * Math.sin(phi), r);
        }
        while (Math.abs(lat - phi) > DELTA);

        latitudeRad = lat;
        longitudeRad = theta;
        altitude = (r / Math.cos(lat)) - Globals.Xkmper * c;
    }

    public double getLatitudeRad() {
        return this.latitudeRad;
    }

    public double getLongitudeRad() {
        return this.longitudeRad;
    }

    public double getAltitude() {
        return this.altitude;
    }

    /// <summary>
    /// Converts to a string representation of the form "38.0N 045.0W 500m".
    /// </summary>
    /// <returns>The formatted string.</returns>
    @Override
    public  String toString()
    {
        boolean latNorth = (latitudeRad >= 0.0);
        boolean lonEast  = (longitudeRad >= 0.0);

        // latitude in degrees
        String str = String.format("{0:00.0}{1} ",
                Math.abs(LatitudeDeg()),
                (latNorth ? 'N' : 'S'));
        // longitude in degrees
        str += String.format("{0:000.0}{1} ",
                Math.abs(LongitudeDeg()),
                (lonEast ? 'E' : 'W'));
        // elevation in meters
        str += String.format("{0:F0}m", altitude * 1000.0);

        return str;
    }
}
