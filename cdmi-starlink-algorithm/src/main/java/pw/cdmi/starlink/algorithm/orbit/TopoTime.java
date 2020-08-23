package pw.cdmi.starlink.algorithm.orbit;

import pw.cdmi.starlink.core.orbit.Julian;
import pw.cdmi.starlink.core.orbit.Topo;

public class TopoTime extends Topo{
    /// <summary>
    /// The time associated with the coordinates.
    /// </summary>
    public Julian date;

    /// <summary>
    /// Creates an instance of the class from topo and time information.
    /// </summary>
    /// <param name="topo"></param>
    /// <param name="date"></param>
    public TopoTime(Topo topo, Julian date)
    {
        super(topo.azimuthRad, topo.elevationRad, topo.range, topo.rangeRate);
        this.date = date;
    }

    /// <summary>
    /// Creates a new instance of the class from the given components.
    /// </summary>
    /// <param name="radAz">Azimuth, in radians.</param>
    /// <param name="radEl">Elevation, in radians.</param>
    /// <param name="range">Range, in kilometers.</param>
    /// <param name="rangeRate">Range rate, in kilometers per second. A negative
    /// range rate means "towards the observer".</param>
    /// <param name="date">The time associated with the coordinates.</param>
    public TopoTime(double radAz, double radEl, double range, double rangeRate, Julian date)
    {
        super(radAz, radEl, range, rangeRate);
        this.date = date;
    }
}
