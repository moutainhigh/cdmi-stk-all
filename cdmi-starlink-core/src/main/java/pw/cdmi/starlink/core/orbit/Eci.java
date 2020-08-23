package pw.cdmi.starlink.core.orbit;

public class Eci {
    public Vector position;
    public Vector velocity;

    /// <summary>
    /// Creates a new instance of the class zero position and zero velocity.
    /// </summary>
    public Eci()
    {
        this(new Vector(), new Vector());
    }

    /// <summary>
    /// Creates a new instance of the class from XYZ coordinates.
    /// </summary>
    /// <param name="pos">The XYZ coordinates.</param>
    public Eci(Vector pos)
    {
        this(pos, new Vector());
    }

    /// <summary>
    /// Creates a new instance of the class with the given position and
    /// velocity components.
    /// </summary>
    /// <param name="pos">The position vector.</param>
    /// <param name="vel">The velocity vector.</param>
    public Eci(Vector pos, Vector vel)
    {
        position = pos;
        velocity = vel;
    }

    /// <summary>
    /// Creates a new instance of the class from ECI coordinates.
    /// </summary>
    /// <param name="eci">The ECI coordinates.</param>
    public Eci(Eci eci)
    {
        position = new Vector(eci.position);
        velocity = new Vector(eci.velocity);
    }

    /// <summary>
    /// Creates a instance of the class from geodetic coordinates.
    /// </summary>
    /// <param name="geo">The geocentric coordinates.</param>
    /// <param name="date">The Julian date.</param>
    /// <remarks>
    /// Assumes the Earth is an oblate spheroid.
    /// Reference: The 1992 Astronomical Almanac, page K11
    /// Reference: www.celestrak.com (Dr. T.S. Kelso)
    /// </remarks>
    public Eci(Geo geo, Julian date)
    {
        double lat = geo.getLatitudeRad();
        double lon = geo.getLongitudeRad();
        double alt = geo.getAltitude();

        // Calculate Local Mean Sidereal Time (theta)
        double theta = date.toLmst(lon);
        double c = 1.0 / Math.sqrt(1.0 + Globals.F * (Globals.F - 2.0) *
                Globals.sqr(Math.sin(lat)));
        double s = Globals.sqr(1.0 - Globals.F) * c;
        double achcp = (Globals.Xkmper * c + alt) * Math.cos(lat);

        position = new Vector();

        position.X = achcp * Math.cos(theta);             // km
        position.Y = achcp * Math.sin(theta);             // km
        position.Z = (Globals.Xkmper * s + alt) * Math.sin(lat);   // km
        position.W = Math.sqrt(Globals.sqr(position.X) +
                Globals.sqr(position.Y) +
                Globals.sqr(position.Z));  // range, km

        velocity = new Vector();
        double mfactor = Globals.TWO_PI * (Globals.OmegaE / Globals.SecPerDay);

        velocity.X = -mfactor * position.Y;               // km / sec
        velocity.Y =  mfactor * position.X;               // km / sec
        velocity.Z = 0.0;                                 // km / sec
        velocity.W = Math.sqrt(Globals.sqr(velocity.X) +  // range rate km/sec^2
                Globals.sqr(velocity.Y));
    }

    /// <summary>
    /// Scale the position vector by a factor.
    /// </summary>
    public void scalePosVector(double factor)
    {
        position.Mul(factor);
    }

    /// <summary>
    /// Scale the velocity vector by a factor.
    /// </summary>
    public void scaleVelVector(double factor)
    {
        velocity.Mul(factor);
    }

    /// <summary>
    /// Returns a string representation of the coordinate and
    /// velocity XYZ values.
    /// </summary>
    /// <returns>The formatted string.</returns>
    @Override
    public String toString()
    {
        return String.format("km:%f,%f,%f km/s:%f, %f, %f",
                position.X, position.Y, position.Z,
                velocity.X, velocity.Y, velocity.Z);
    }
}
