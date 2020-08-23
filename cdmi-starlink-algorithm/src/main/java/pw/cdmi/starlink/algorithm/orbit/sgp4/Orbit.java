package pw.cdmi.starlink.algorithm.orbit.sgp4;

import java.util.Date;

import pw.cdmi.starlink.algorithm.orbit.EciTime;
import pw.cdmi.starlink.core.datetime.TimeSpan;
import pw.cdmi.starlink.core.orbit.Globals;
import pw.cdmi.starlink.core.orbit.Julian;
import pw.cdmi.starlink.core.orbit.Tle;

public class Orbit {
    private TimeSpan m_Period = new TimeSpan(0, 0, 0, 0);

    // TLE caching variables
    private double m_Inclination;
    private double m_Eccentricity;
    private double m_RAAN;
    private double m_ArgPerigee;
    private double m_BStar;
    private double m_Drag;
    private double m_MeanAnomaly;
    private double m_TleMeanMotion;

    // Caching variables recovered from the input TLE elements
    private double m_aeAxisSemiMajorRec;  // semimajor axis, in AE units
    private double m_aeAxisSemiMinorRec;  // semiminor axis, in AE units
    private double m_rmMeanMotionRec;     // radians per minute
    private double m_kmPerigeeRec;        // perigee, in km
    private double m_kmApogeeRec;         // apogee, in km


    private Tle tle;
    public String TleLine1() { return tle.getLine1(); };
    public String TleLine2() { return tle.getLine2(); };

    public Julian epoch;
    public Date epochTime() { return epoch.toTime(); };

    private NoradBase NoradModel;

    // "Recovered" from the input elements
    public double SemiMajor()    { return m_aeAxisSemiMajorRec; }
    public double SemiMinor()    { return m_aeAxisSemiMinorRec; }
    public double MeanMotion()   { return m_rmMeanMotionRec;    }
    public double Major()       { return 2.0 * SemiMajor();      }
    public double Minor()        { return 2.0 * SemiMinor();      }
    public double Perigee()      { return m_kmPerigeeRec;       }
    public double Apogee()       { return m_kmApogeeRec;        }

    public double Inclination()    { return m_Inclination;   }
    public double Eccentricity()   { return m_Eccentricity;  }
    public double RAAN()           { return m_RAAN;          }
    public double ArgPerigee()     { return m_ArgPerigee;    }
    public double BStar()          { return m_BStar;         }
    public double Drag()          { return m_Drag;          }
    public double MeanAnomaly()    { return m_MeanAnomaly;   }
    private double TleMeanMotion() { return m_TleMeanMotion; }

    public String SatNoradId()    { return tle.getNoradNum(); }
    public String SatName()       { return tle.getName();        }
    public String SatNameLong()   { return SatName() + " #" + SatNoradId(); }

    public TimeSpan Period()
    {
        if (m_Period.getTotalSeconds() < 0.0)
        {
            // Calculate the period using the recovered mean motion.
            if (MeanMotion() == 0)
            {
                m_Period = new TimeSpan(0, 0, 0);
            }
            else
            {
                double secs  = (Globals.TWO_PI / MeanMotion()) * 60.0;
                int    msecs = (int)((secs - (int)secs) * 1000);

                m_Period = new TimeSpan(0, 0, 0, (int)secs, msecs);
            }
        }

        return m_Period;
    }

    /// <summary>
    /// Standard constructor.
    /// </summary>
    /// <param name="tle">Two-line element orbital parameters.</param>
    public Orbit(Tle tle) {
        this.tle     = tle;
        this.epoch = tle.EpochJulian();

        m_Inclination   = Globals.toRadians(tle.getIncl());
        m_Eccentricity  = tle.getEccn();
        m_RAAN          = Globals.toRadians(tle.getRaan());
        m_ArgPerigee    = Globals.toRadians(tle.getArgper());
        m_BStar         = tle.getBstar();
        m_Drag          = tle.getNdot();
        m_MeanAnomaly   = Globals.toRadians(tle.getMeanan());
        m_TleMeanMotion = tle.getMeanmo();
        // Recover the original mean motion and semimajor axis from the
        // input elements.
        double mm     = TleMeanMotion();
        double rpmin  = mm * Globals.TWO_PI / Globals.MinPerDay;   // rads per minute

        double a1     = Math.pow(Globals.Xke / rpmin, 2.0 / 3.0);
        double e      = Eccentricity();
        double i      = Inclination();
        double temp   = (1.5 * Globals.Ck2 * (3.0 * Globals.sqr(Math.cos(i)) - 1.0) /
                Math.pow(1.0 - e * e, 1.5));
        double delta1 = temp / (a1 * a1);
        double a0     = a1 *
                (1.0 - delta1 *
                        ((1.0 / 3.0) + delta1 *
                                (1.0 + 134.0 / 81.0 * delta1)));

        double delta0 = temp / (a0 * a0);

        m_rmMeanMotionRec    = rpmin / (1.0 + delta0);
        m_aeAxisSemiMajorRec = a0 / (1.0 - delta0);
        m_aeAxisSemiMinorRec = m_aeAxisSemiMajorRec * Math.sqrt(1.0 - (e * e));
        m_kmPerigeeRec       = Globals.Xkmper * (m_aeAxisSemiMajorRec * (1.0 - e) - Globals.Ae);
        m_kmApogeeRec        = Globals.Xkmper * (m_aeAxisSemiMajorRec * (1.0 + e) - Globals.Ae);

        if (Period().getTotalMinutes() >= 225.0)
        {
            // SDP4 - period >= 225 minutes.
            NoradModel = new NoradSDP4(this);
        }
        else
        {
            // SGP4 - period < 225 minutes
            NoradModel = new NoradSGP4(this);
        }
    }


    /// <summary>
    /// Calculate satellite ECI position/velocity for a given time.
    /// </summary>
    /// <param name="mpe">Target time, in minutes past the TLE epoch.</param>
    /// <returns>Kilometer-based position/velocity ECI coordinates.</returns>
    public EciTime positionEci(double mpe)
    {
        EciTime eci = NoradModel.getPosition(mpe);

        // Convert ECI vector units from AU to kilometers
        double radiusAe = Globals.Xkmper / Globals.Ae;

        eci.scalePosVector(radiusAe);                               // km
        eci.scaleVelVector(radiusAe * (Globals.MinPerDay / 86400)); // km/sec

        return eci;
    }

    /// <summary>
    /// Calculate ECI position/velocity for a given time.
    /// </summary>
    /// <param name="utc">Target time (UTC).</param>
    /// <returns>Kilometer-based position/velocity ECI coordinates.</returns>
    public EciTime positionEci(Date utc)
    {
        return positionEci(tPlusEpoch(utc).getTotalMinutes());
    }

    /// <summary>
    /// Calculate satellite ECI position/velocity for a given time.
    /// </summary>
    /// <param name="mpe">Target time, in minutes past the TLE epoch.</param>
    /// <returns>Kilometer-based position/velocity ECI coordinates.</returns>
    public EciTime GetPosition(double mpe)
    {
        return positionEci(mpe);
    }

    /// <summary>
    /// Calculate ECI position/velocity for a given time.
    /// </summary>
    /// <param name="utc">Target time (UTC).</param>
    /// <returns>Kilometer-based position/velocity ECI coordinates.</returns>
    public EciTime GetPosition(Date utc)
    {
        return positionEci(tPlusEpoch(utc).getTotalMinutes());
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Returns elapsed time from epoch to given time.
    // Note: "Predicted" TLEs can have epochs in the future.
    public TimeSpan tPlusEpoch(Date utc)
    {
        return new TimeSpan(utc.getTime() - epochTime().getTime());
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Returns elapsed time from epoch to current time.
    // Note: "Predicted" TLEs can have epochs in the future.
    public TimeSpan tPlusEpoch()
    {
        return tPlusEpoch(new Date());
    }
    
    public Date getEpochTime() {
    	return tle.getEpochTime();
    }

}
