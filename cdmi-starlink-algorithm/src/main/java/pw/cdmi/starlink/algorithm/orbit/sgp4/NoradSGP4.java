package pw.cdmi.starlink.algorithm.orbit.sgp4;

import pw.cdmi.starlink.algorithm.orbit.EciTime;
import pw.cdmi.starlink.core.orbit.Globals;

public class NoradSGP4 extends NoradBase {
    private double m_c5 =0.0D;
    private double m_omgcof = 0.0D;
    private double m_xmcof =0.0D;
    private double m_delmo = 0.0D;
    private double m_sinmo = 0.0D;

    // ///////////////////////////////////////////////////////////////////////////
    public NoradSGP4(Orbit orbit)
    {
        super(orbit);
        m_c5     = 2.0 * m_coef1 * m_aodp * m_betao2 *
                (1.0 + 2.75 * (m_etasq + m_eeta) + m_eeta * m_etasq);
        m_omgcof = this.orbit.BStar() * m_c3 * Math.cos(this.orbit.ArgPerigee());
        m_xmcof  = -(2.0 / 3.0) * m_coef * this.orbit.BStar() * Globals.Ae / m_eeta;
        m_delmo  = Math.pow(1.0 + m_eta * Math.cos(this.orbit.MeanAnomaly()), 3.0);
        m_sinmo  = Math.sin(this.orbit.MeanAnomaly());
    }

    /// <summary>
    /// Calculate satellite ECI position/velocity for a given time.
    /// </summary>
    /// <param name="tsince">Target time, in minutes-past-epoch format.</param>
    /// <returns>AU-based position/velocity ECI coordinates.</returns>
    /// <remarks>
    /// This procedure returns the ECI position and velocity for the satellite
    /// in the orbit at the given number of minutes since the TLE epoch time.
    /// The algorithm uses NORAD's Simplified General Perturbation 4 near earth
    /// orbit model.
    /// </remarks>
    @Override
    public EciTime getPosition(double tsince)
    {
        // For m_perigee less than 220 kilometers, the isimp flag is set and
        // the equations are truncated to linear variation in square root of a
        // and quadratic variation in mean anomaly.  Also, the m_c3 term, the
        // delta omega term, and the delta m term are dropped.
        boolean isimp = false;

        if ((m_aodp * (1.0 - m_satEcc) / Globals.Ae) < (220.0 / Globals.Xkmper + Globals.Ae))
        {
            isimp = true;
        }

        double d2 = 0.0;
        double d3 = 0.0;
        double d4 = 0.0;

        double t3cof = 0.0;
        double t4cof = 0.0;
        double t5cof = 0.0;

        if (!isimp)
        {
            double c1sq = m_c1 * m_c1;

            d2 = 4.0 * m_aodp * m_tsi * c1sq;

            double temp = d2 * m_tsi * m_c1 / 3.0;

            d3 = (17.0 * m_aodp + m_s4) * temp;
            d4 = 0.5 * temp * m_aodp * m_tsi *
                    (221.0 * m_aodp + 31.0 * m_s4) * m_c1;
            t3cof = d2 + 2.0 * c1sq;
            t4cof = 0.25 * (3.0 * d3 + m_c1 * (12.0 * d2 + 10.0 * c1sq));
            t5cof = 0.2 * (3.0 * d4 + 12.0 * m_c1 * d3 + 6.0 *
                    d2 * d2 + 15.0 * c1sq * (2.0 * d2 + c1sq));
        }

        // Update for secular gravity and atmospheric drag.
        double xmdf   = orbit.MeanAnomaly() + m_xmdot * tsince;
        double omgadf = orbit.ArgPerigee() + m_omgdot * tsince;
        double xnoddf = orbit.RAAN() + m_xnodot * tsince;
        double omega  = omgadf;
        double xmp    = xmdf;
        double tsq    = tsince * tsince;
        double xnode  = xnoddf + m_xnodcf * tsq;
        double tempa  = 1.0 - m_c1 * tsince;
        double tempe  = orbit.BStar() * m_c4 * tsince;
        double templ  = m_t2cof * tsq;

        if (!isimp)
        {
            double delomg = m_omgcof * tsince;
            double delm = m_xmcof * (Math.pow(1.0 + m_eta * Math.cos(xmdf), 3.0) - m_delmo);
            double temp = delomg + delm;

            xmp = xmdf + temp;
            omega = omgadf - temp;

            double tcube = tsq * tsince;
            double tfour = tsince * tcube;

            tempa = tempa - d2 * tsq - d3 * tcube - d4 * tfour;
            tempe = tempe + orbit.BStar() * m_c5 * (Math.sin(xmp) - m_sinmo);
            templ = templ + t3cof * tcube + tfour * (t4cof + tsince * t5cof);
        }

        double a  = m_aodp * Globals.sqr(tempa);
        double e  = m_satEcc - tempe;

        double xl = xmp + omega + xnode + m_xnodp * templ;
        double xn = Globals.Xke / Math.pow(a, 1.5);

        return finalPosition(m_satInc, omgadf, e, a, xl, xnode, xn, tsince);
    }
}
