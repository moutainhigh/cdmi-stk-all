package pw.cdmi.starlink.core.orbit;

public class Topo {
    /// <summary>
    /// The azimuth, in radians.
    /// </summary>
    public double azimuthRad;

    /// <summary>
    /// The elevation, in radians.
    /// </summary>
    public double elevationRad;

    /// <summary>
    /// The azimuth, in degrees.
    /// </summary>
    public double AzimuthDeg(){ return Globals.toDegrees(azimuthRad); }

    /// <summary>
    /// The elevation, in degrees.
    /// </summary>
    public double ElevationDeg() {return Globals.toDegrees(elevationRad); }

    /// <summary>
    /// The range, in kilometers.
    /// </summary>
    public double range;

    /// <summary>
    /// The range rate, in kilometers per second.
    /// A negative value means "towards observer".
    /// </summary>
    public double rangeRate;


    /// <summary>
    /// Creates a new instance of the class from the given components.
    /// </summary>
    /// <param name="radAz">Azimuth, in radians.</param>
    /// <param name="radEl">Elevation, in radians.</param>
    /// <param name="range">Range, in kilometers.</param>
    /// <param name="rangeRate">Range rate, in kilometers per second. A negative
    /// range rate means "towards the observer".</param>
    public Topo(double radAz, double radEl, double range, double rangeRate)
    {
        azimuthRad = radAz;
        elevationRad = radEl;
        this.range = range;
        this.rangeRate = rangeRate;
    }

}
