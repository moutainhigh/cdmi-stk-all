package pw.cdmi.starlink.core.orbit;

public class Globals {
    public static final double PI = 3.141592653589793;
    public static final double TWO_PI = 2.0 * PI;            // = 2.0 * PI
    public static final double RadsPerDegree = 0.0174532925199433;  // = PI/180
    public static final double DegreesPerRad = 180.0 / PI;          // = 180 /PI

    public static final double Gm          = 398601.2;   // Earth gravitational constant, km^3/sec^2
    public static final double GeoSyncAlt  = 42241.892;  // km
    public static final double EarthDiam   = 12800.0;    // km
    public static final double DaySidereal = (23 * 3600) + (56 * 60) + 4.09;  // sec
    public static final double DaySolar    = (24 * 3600);   // sec

    public static final double Ae          = 1.0;
    public static final double Au          = 149597870.0;  // Astronomical unit (km) (IAU 76)
    public static final double Sr          = 696000.0;     // Solar radius (km)      (IAU 76)
    public static final double Xkmper      = 6378.135;     // Earth equatorial radius - kilometers (WGS '72)
    public static final double F           = 1.0 / 298.26; // Earth flattening (WGS '72)
    public static final double Ge          = 398600.8;     // Earth gravitational constant (WGS '72)
    public static final double J2          = 1.0826158E-3; // J2 harmonic (WGS '72)
    public static final double J3          = -2.53881E-6;  // J3 harmonic (WGS '72)
    public static final double J4          = -1.65597E-6;  // J4 harmonic (WGS '72)
    public static final double Ck2         = J2 / 2.0;
    public static final double Ck4         = -3.0 * J4 / 8.0;
    public static final double Xj3         = J3;
    public static final double Qo          = Globals.Ae + 120.0 / Globals.Xkmper;
    public static final double S           = Globals.Ae + 78.0  / Globals.Xkmper;
    public static final int HoursPerDay    = 24;          // Hours per day   (solar)
    public static final int MinPerDay      = 1440;        // Minutes per day (solar)
    public static final int SecPerDay      = 86400;       // Seconds per day (solar)
    public static final int SecPerHour     = 3600;        // Seconds per Hour(solar)
    public static final int SecPerMin	   = 60;		  // Seconds per Minute
    /***每个恒星日地球所旋转的圈数，或者说是1个恒星日=1.00273790934个太阳日**/
    public static final double OmegaE      = 1.00273790934; // Earth rotation per sidereal day
    public static final double Xke         = Math.sqrt(3600.0 * Ge /
            (Globals.Xkmper * Globals.Xkmper * Globals.Xkmper)); // sqrt(ge) ER^3/min^2
    public static final double Qoms2t     = Math.pow((Qo - Globals.S), 4); //(QO - S)^4 ER^4



    public static double sqr(double x)
    {
        return (x * x);
    }

    public static double fmod2p(double arg)
    {
        double modu = (arg % TWO_PI);

        if (modu < 0.0)
        {
            modu += TWO_PI;
        }

        return modu;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Globals.AcTan()
    // ArcTangent of sin(x) / cos(x). The advantage of this function over arctan()
    // is that it returns the correct quadrant of the angle.
    public static double acTan(double sinx, double cosx)
    {
        double ret;

        if (cosx == 0.0)
        {
            if (sinx > 0.0)
            {
                ret = PI / 2.0;
            }
            else
            {
                ret = 3.0 * PI / 2.0;
            }
        }
        else
        {
            if (cosx > 0.0)
            {
                ret = Math.atan(sinx / cosx);
            }
            else
            {
                ret = PI + Math.atan(sinx / cosx);
            }
        }

        return ret;
    }

    public static double toDegrees(double radians)
    {
        return radians * DegreesPerRad;
    }

    public static double toRadians(double degrees)
    {
        return degrees * RadsPerDegree;
    }
}
