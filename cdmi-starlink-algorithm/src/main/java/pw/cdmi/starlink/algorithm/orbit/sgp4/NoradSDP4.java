package pw.cdmi.starlink.algorithm.orbit.sgp4;

import pw.cdmi.starlink.algorithm.orbit.EciTime;
import pw.cdmi.starlink.core.orbit.Globals;
import pw.cdmi.starlink.core.orbit.Julian;

public class NoradSDP4 extends NoradBase {
    static final double zns  =  1.19459E-5;
    static final double zes  =  0.01675;
    static final double znl  =  1.5835218E-4;
    static final double zel  =  0.05490;
    static final double thdt =  4.3752691E-3;

    double dp_e3;     double dp_ee2;    double dp_se2;
    double dp_se3;    double dp_sgh2;   double dp_sgh3;   double dp_sgh4;
    double dp_sh2;    double dp_sh3;    double dp_si2;
    double dp_si3;    double dp_sl2;    double dp_sl3;    double dp_sl4;
    double dp_xgh2;   double dp_xgh3;   double dp_xgh4;   double dp_xh2;
    double dp_xh3;    double dp_xi2;    double dp_xi3;    double dp_xl2;
    double dp_xl3;    double dp_xl4;    double dp_xqncl;  double dp_zmol;
    double dp_zmos;

    double dp_atime;  double dp_d2201;  double dp_d2211;  double dp_d3210;
    double dp_d3222;  double dp_d4410;  double dp_d4422;  double dp_d5220;
    double dp_d5232;  double dp_d5421;  double dp_d5433;  double dp_del1;
    double dp_del2;   double dp_del3;   double dp_omegaq; double dp_sse;
    double dp_ssg;    double dp_ssh;    double dp_ssi;    double dp_ssl;
    double dp_step2;  double dp_stepn;  double dp_stepp;  double dp_thgr;
    double dp_xfact;  double dp_xlamo;  double dp_xli;    double dp_xni;

    boolean gp_reso;  // geopotential resonant
    boolean gp_sync;  // geopotential synchronous

    // ///////////////////////////////////////////////////////////////////////////
    public NoradSDP4(Orbit orbit)
    {
        super(orbit);
        double sinarg = Math.sin(this.orbit.ArgPerigee());
        double cosarg = Math.cos(this.orbit.ArgPerigee());

        // Deep space initialization
        Julian jd = this.orbit.epoch;

        dp_thgr = jd.toGmst();

        double eq = this.orbit.Eccentricity();
        double aqnv = 1.0 / this.orbit.SemiMajor();

        dp_xqncl = this.orbit.Inclination();

        double xmao = this.orbit.MeanAnomaly();
        double xpidot = m_omgdot + m_xnodot;
        double sinq = Math.sin(this.orbit.RAAN());
        double cosq = Math.cos(this.orbit.RAAN());

        dp_omegaq = this.orbit.ArgPerigee();


        // Initialize lunar solar terms
        double day = jd.fromJan0_12h_1900();

        double dpi_xnodce = 4.5236020 - 9.2422029E-4 * day;
        double dpi_stem = Math.sin(dpi_xnodce);
        double dpi_ctem = Math.cos(dpi_xnodce);
        double dpi_zcosil = 0.91375164 - 0.03568096 * dpi_ctem;
        double dpi_zsinil = Math.sqrt(1.0 - dpi_zcosil * dpi_zcosil);
        double dpi_zsinhl = 0.089683511 * dpi_stem / dpi_zsinil;
        double dpi_zcoshl = Math.sqrt(1.0 - dpi_zsinhl * dpi_zsinhl);
        double dpi_c = 4.7199672 + 0.22997150 * day;
        double dpi_gam = 5.8351514 + 0.0019443680 * day;

        dp_zmol = Globals.fmod2p(dpi_c - dpi_gam);

        double dpi_zx = 0.39785416 * dpi_stem / dpi_zsinil;
        double dpi_zy = dpi_zcoshl * dpi_ctem + 0.91744867 * dpi_zsinhl * dpi_stem;

        dpi_zx = Globals.acTan(dpi_zx, dpi_zy) + dpi_gam - dpi_xnodce;

        double dpi_zcosgl = Math.cos(dpi_zx);
        double dpi_zsingl = Math.sin(dpi_zx);

        dp_zmos = 6.2565837 + 0.017201977 * day;
        dp_zmos = Globals.fmod2p(dp_zmos);

        final double zcosis = 0.91744867;
        final double zsinis = 0.39785416;
        final double zsings = -0.98088458;
        final double zcosgs = 0.1945905;
        final double c1ss = 2.9864797E-6;

        double zcosg = zcosgs;
        double zsing = zsings;
        double zcosi = zcosis;
        double zsini = zsinis;
        double zcosh = cosq;
        double zsinh = sinq;
        double cc = c1ss;
        double zn = zns;
        double ze = zes;
        double xnoi = 1.0 / this.orbit.MeanMotion();

        double a1;  double a3;  double a7;  double a8;  double a9;  double a10;
        double a2;  double a4;  double a5;  double a6;  double x1;  double x2;
        double x3;  double x4;  double x5;  double x6;  double x7;  double x8;
        double z31; double z32; double z33; double z1;  double z2;  double z3;
        double z11; double z12; double z13; double z21; double z22; double z23;
        double s3;  double s2;  double s4;  double s1;  double s5;  double s6;
        double s7;

        double se  = 0.0;
        double si  = 0.0;
        double sl  = 0.0;
        double sgh = 0.0;
        double sh  = 0.0;

        double eosq = Globals.sqr(this.orbit.Eccentricity());

        // Apply the solar and lunar terms on the first pass, then re-apply the
        // solar terms again on the second pass.

        for (int pass = 1; pass <= 2; pass++)
        {
            // Do solar terms
            a1 =  zcosg * zcosh + zsing * zcosi * zsinh;
            a3 = -zsing * zcosh + zcosg * zcosi * zsinh;
            a7 = -zcosg * zsinh + zsing * zcosi * zcosh;
            a8 = zsing * zsini;
            a9 = zsing * zsinh + zcosg * zcosi * zcosh;
            a10 = zcosg * zsini;
            a2 = m_cosio * a7 + m_sinio * a8;
            a4 = m_cosio * a9 + m_sinio * a10;
            a5 = -m_sinio * a7 + m_cosio * a8;
            a6 = -m_sinio * a9 + m_cosio * a10;
            x1 = a1 * cosarg + a2 * sinarg;
            x2 = a3 * cosarg + a4 * sinarg;
            x3 = -a1 * sinarg + a2 * cosarg;
            x4 = -a3 * sinarg + a4 * cosarg;
            x5 = a5 * sinarg;
            x6 = a6 * sinarg;
            x7 = a5 * cosarg;
            x8 = a6 * cosarg;
            z31 = 12.0 * x1 * x1 - 3.0 * x3 * x3;
            z32 = 24.0 * x1 * x2 - 6.0 * x3 * x4;
            z33 = 12.0 * x2 * x2 - 3.0 * x4 * x4;
            z1 = 3.0 * (a1 * a1 + a2 * a2) + z31 * eosq;
            z2 = 6.0 * (a1 * a3 + a2 * a4) + z32 * eosq;
            z3 = 3.0 * (a3 * a3 + a4 * a4) + z33 * eosq;
            z11 = -6.0 * a1 * a5 + eosq * (-24.0 * x1 * x7 - 6.0 * x3 * x5);
            z12 = -6.0 * (a1 * a6 + a3 * a5) +
                    eosq * (-24.0 * (x2 * x7 + x1 * x8) - 6.0 * (x3 * x6 + x4 * x5));
            z13 = -6.0 * a3 * a6 + eosq * (-24.0 * x2 * x8 - 6.0 * x4 * x6);
            z21 = 6.0 * a2 * a5 + eosq * (24.0 * x1 * x5 - 6.0 * x3 * x7);
            z22 = 6.0 * (a4 * a5 + a2 * a6) +
                    eosq * (24.0 * (x2 * x5 + x1 * x6) - 6.0 * (x4 * x7 + x3 * x8));
            z23 = 6.0 * a4 * a6 + eosq * (24.0 * x2 * x6 - 6.0 * x4 * x8);
            z1 = z1 + z1 + m_betao2 * z31;
            z2 = z2 + z2 + m_betao2 * z32;
            z3 = z3 + z3 + m_betao2 * z33;
            s3 = cc * xnoi;
            s2 = -0.5 * s3 / m_betao;
            s4 = s3 * m_betao;
            s1 = -15.0 * eq * s4;
            s5 = x1 * x3 + x2 * x4;
            s6 = x2 * x3 + x1 * x4;
            s7 = x2 * x4 - x1 * x3;
            se = s1 * zn * s5;
            si = s2 * zn * (z11 + z13);
            sl = -zn * s3 * (z1 + z3 - 14.0 - 6.0 * eosq);
            sgh = s4 * zn * (z31 + z33 - 6.0);

            if (this.orbit.Inclination() < 5.2359877E-2)
            {
                sh = 0.0;
            }
            else
            {
                sh = -zn * s2 * (z21 + z23);
            }

            dp_ee2 = 2.0 * s1 * s6;
            dp_e3 = 2.0 * s1 * s7;
            dp_xi2 = 2.0 * s2 * z12;
            dp_xi3 = 2.0 * s2 * (z13 - z11);
            dp_xl2 = -2.0 * s3 * z2;
            dp_xl3 = -2.0 * s3 * (z3 - z1);
            dp_xl4 = -2.0 * s3 * (-21.0 - 9.0 * eosq) * ze;
            dp_xgh2 = 2.0 * s4 * z32;
            dp_xgh3 = 2.0 * s4 * (z33 - z31);
            dp_xgh4 = -18.0 * s4 * ze;
            dp_xh2 = -2.0 * s2 * z22;
            dp_xh3 = -2.0 * s2 * (z23 - z21);

            if (pass == 1)
            {
                // Do lunar terms
                dp_sse = se;
                dp_ssi = si;
                dp_ssl = sl;
                dp_ssh = sh / m_sinio;
                dp_ssg = sgh - m_cosio * dp_ssh;
                dp_se2 = dp_ee2;
                dp_si2 = dp_xi2;
                dp_sl2 = dp_xl2;
                dp_sgh2 = dp_xgh2;
                dp_sh2 = dp_xh2;
                dp_se3 = dp_e3;
                dp_si3 = dp_xi3;
                dp_sl3 = dp_xl3;
                dp_sgh3 = dp_xgh3;
                dp_sh3 = dp_xh3;
                dp_sl4 = dp_xl4;
                dp_sgh4 = dp_xgh4;
                zcosg = dpi_zcosgl;
                zsing = dpi_zsingl;
                zcosi = dpi_zcosil;
                zsini = dpi_zsinil;
                zcosh = dpi_zcoshl * cosq + dpi_zsinhl * sinq;
                zsinh = sinq * dpi_zcoshl - cosq * dpi_zsinhl;
                zn = znl;

               final double c1l =  4.7968065E-7;

                cc = c1l;
                ze = zel;
            }
        }


        dp_sse = dp_sse + se;
        dp_ssi = dp_ssi + si;
        dp_ssl = dp_ssl + sl;
        dp_ssg = dp_ssg + sgh - m_cosio / m_sinio * sh;
        dp_ssh = dp_ssh + sh / m_sinio;

        // Geopotential resonance initialization for 12 hour orbits
        gp_reso = false;
        gp_sync = false;

        double g310;
        double f220;
        double bfact = 0.0;

        // Determine if orbit is 12- or 24-hour resonant.
        // Mean motion is given in radians per minute.
        if ((this.orbit.MeanMotion() > 0.0034906585) && (this.orbit.MeanMotion() < 0.0052359877))
        {
            // Orbit is within the Clarke Belt (period is 24-hour resonant).
            // Synchronous resonance terms initialization
            gp_reso = true;
            gp_sync = true;


            double g200 = 1.0 + eosq * (-2.5 + 0.8125 * eosq);

            g310 = 1.0 + 2.0 * eosq;

            double g300 = 1.0 + eosq * (-6.0 + 6.60937 * eosq);

            f220 = 0.75 * (1.0 + m_cosio) * (1.0 + m_cosio);

            double f311 = 0.9375 * m_sinio * m_sinio * (1.0 + 3 * m_cosio) - 0.75 * (1.0 + m_cosio);
            double f330 = 1.0 + m_cosio;

            f330 = 1.875 * f330 * f330 * f330;

            final double q22 = 1.7891679e-06;
            final double q33 = 2.2123015e-07;
            final double q31 = 2.1460748e-06;

            dp_del1 = 3.0 * m_xnodp * m_xnodp * aqnv * aqnv;
            dp_del2 = 2.0 * dp_del1 * f220 * g200 * q22;
            dp_del3 = 3.0 * dp_del1 * f330 * g300 * q33 * aqnv;

            dp_del1 = dp_del1 * f311 * g310 * q31 * aqnv;
            dp_xlamo = xmao + this.orbit.RAAN() + this.orbit.ArgPerigee() - dp_thgr;
            bfact = m_xmdot + xpidot - thdt;
            bfact = bfact + dp_ssl + dp_ssg + dp_ssh;

        }
        else if (((this.orbit.MeanMotion() >= 8.26E-3) && (this.orbit.MeanMotion() <= 9.24E-3)) && (eq >= 0.5))
        {
            // Period is 12-hour resonant
            gp_reso = true;

            double eoc  = eq * eosq;
            double g201 = -0.306 - (eq - 0.64) * 0.440;

            double g211; double g322;
            double g410; double g422;
            double g520;

            if (eq <= 0.65)
            {
                g211 =   3.616   -   13.247  * eq +   16.290 * eosq;
                g310 =  -19.302  +  117.390  * eq -  228.419 * eosq +  156.591 * eoc;
                g322 =  -18.9068 +  109.7927 * eq - 214.6334 * eosq + 146.5816 * eoc;
                g410 =  -41.122  +  242.694  * eq -  471.094 * eosq +  313.953 * eoc;
                g422 = -146.407  +  841.880  * eq - 1629.014 * eosq + 1083.435 * eoc;
                g520 = -532.114  + 3017.977  * eq -   5740.0 * eosq + 3708.276 * eoc;
            }
            else
            {
                g211 =   -72.099 +  331.819 * eq -  508.738 * eosq +  266.724 * eoc;
                g310 =  -346.844 + 1582.851 * eq - 2415.925 * eosq + 1246.113 * eoc;
                g322 =  -342.585 + 1554.908 * eq - 2366.899 * eosq + 1215.972 * eoc;
                g410 = -1052.797 + 4758.686 * eq - 7193.992 * eosq + 3651.957 * eoc;
                g422 =  -3581.69 + 16178.11 * eq - 24462.77 * eosq + 12422.52 * eoc;

                if (eq <= 0.715)
                {
                    g520 = 1464.74 - 4664.75 * eq + 3763.64 * eosq;
                }
                else
                {
                    g520 = -5149.66 + 29936.92 * eq - 54087.36 * eosq + 31324.56 * eoc;
                }
            }

            double g533;
            double g521;
            double g532;

            if (eq < 0.7)
            {
                g533 = -919.2277  + 4988.61   * eq - 9064.77   * eosq + 5542.21  * eoc;
                g521 = -822.71072 + 4568.6173 * eq - 8491.4146 * eosq + 5337.524 * eoc;
                g532 = -853.666   + 4690.25   * eq - 8624.77   * eosq + 5341.4   * eoc;
            }
            else
            {
                g533 = -37995.78  + 161616.52 * eq - 229838.2  * eosq + 109377.94 * eoc;
                g521 = -51752.104 + 218913.95 * eq - 309468.16 * eosq + 146349.42 * eoc;
                g532 = -40023.88  + 170470.89 * eq - 242699.48 * eosq + 115605.82 * eoc;
            }

            double sini2 = m_sinio * m_sinio;
            double cosi2 = m_cosio * m_cosio;

            f220 = 0.75 * (1.0 + 2.0 * m_cosio + cosi2);

            double f221 = 1.5 * sini2;
            double f321 = 1.875 * m_sinio * (1.0 - 2.0 * m_cosio - 3.0 * cosi2);
            double f322 = -1.875 * m_sinio * (1.0 + 2.0 * m_cosio - 3.0 * cosi2);
            double f441 = 35.0 * sini2 * f220;
            double f442 = 39.3750 * sini2 * sini2;
            double f522 = 9.84375 * m_sinio * (sini2 * (1.0 - 2.0 * m_cosio - 5.0 * cosi2) +
                    0.33333333 * (-2.0 + 4.0 * m_cosio + 6.0 * cosi2));
            double f523 = m_sinio * (4.92187512 * sini2 * (-2.0 - 4.0 * m_cosio + 10.0 * cosi2) +
                    6.56250012 * (1.0 + 2.0 * m_cosio - 3.0 * cosi2));
            double f542 = 29.53125 * m_sinio * (2.0 - 8.0 * m_cosio + cosi2 * (-12.0 + 8.0 * m_cosio + 10.0 * cosi2));
            double f543 = 29.53125 * m_sinio * (-2.0 - 8.0 * m_cosio + cosi2 * (12.0 + 8.0 * m_cosio - 10.0 * cosi2));
            double xno2 = m_xnodp * m_xnodp;
            double ainv2 = aqnv * aqnv;
            double temp1 = 3.0 * xno2 * ainv2;

            final double root22 = 1.7891679E-6;
            final double root32 = 3.7393792E-7;
            final double root44 = 7.3636953E-9;
            final double root52 = 1.1428639E-7;
            final double root54 = 2.1765803E-9;

            double temp = temp1 * root22;

            dp_d2201 = temp * f220 * g201;
            dp_d2211 = temp * f221 * g211;
            temp1 = temp1 * aqnv;
            temp = temp1 * root32;
            dp_d3210 = temp * f321 * g310;
            dp_d3222 = temp * f322 * g322;
            temp1 = temp1 * aqnv;
            temp = 2.0 * temp1 * root44;
            dp_d4410 = temp * f441 * g410;
            dp_d4422 = temp * f442 * g422;
            temp1 = temp1 * aqnv;
            temp = temp1 * root52;
            dp_d5220 = temp * f522 * g520;
            dp_d5232 = temp * f523 * g532;
            temp = 2.0 * temp1 * root54;
            dp_d5421 = temp * f542 * g521;
            dp_d5433 = temp * f543 * g533;
            dp_xlamo = xmao + this.orbit.RAAN() + this.orbit.RAAN() - dp_thgr - dp_thgr;
            bfact = m_xmdot + m_xnodot + m_xnodot - thdt - thdt;
            bfact = bfact + dp_ssl + dp_ssh + dp_ssh;

        }

        if (gp_reso || gp_sync)
        {
            dp_xfact = bfact - m_xnodp;

            // Initialize integrator
            dp_xli = dp_xlamo;
            dp_xni = m_xnodp;
            // dp_atime = 0.0; // performed by runtime
            dp_stepp = 720.0;
            dp_stepn = -720.0;
            dp_step2 = 259200.0;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    private boolean deepCalcDotTerms(double pxndot, double pxnddt, double pxldot)
    {
        // Dot terms calculated
        if (gp_sync)
        {
            final double fasx2 = 0.13130908;
            final double fasx4 = 2.8843198;
            final double fasx6 = 0.37448087;

            pxndot = dp_del1 * Math.sin(dp_xli - fasx2) +
                    dp_del2 * Math.sin(2.0 * (dp_xli - fasx4)) +
                    dp_del3 * Math.sin(3.0 * (dp_xli - fasx6));
            pxnddt = dp_del1 * Math.cos(dp_xli - fasx2) +
                    2.0 * dp_del2 * Math.cos(2.0 * (dp_xli - fasx4)) +
                    3.0 * dp_del3 * Math.cos(3.0 * (dp_xli - fasx6));
        }
        else
        {
            final double g54 = 4.4108898;
            final double g52 = 1.0508330;
            final double g44 = 1.8014998;
            final double g22 = 5.7686396;
            final double g32 = 0.95240898;

            double xomi  = dp_omegaq + m_omgdot * dp_atime;
            double x2omi = xomi + xomi;
            double x2li  = dp_xli + dp_xli;

            pxndot = dp_d2201 * Math.sin(x2omi + dp_xli - g22) +
                    dp_d2211 * Math.sin(dp_xli - g22)         +
                    dp_d3210 * Math.sin( xomi + dp_xli - g32) +
                    dp_d3222 * Math.sin(-xomi + dp_xli - g32) +
                    dp_d4410 * Math.sin(x2omi + x2li - g44)   +
                    dp_d4422 * Math.sin(x2li - g44)           +
                    dp_d5220 * Math.sin( xomi + dp_xli - g52) +
                    dp_d5232 * Math.sin(-xomi + dp_xli - g52) +
                    dp_d5421 * Math.sin( xomi + x2li - g54)   +
                    dp_d5433 * Math.sin(-xomi + x2li - g54);

            pxnddt = dp_d2201 * Math.cos(x2omi + dp_xli - g22) +
                    dp_d2211 * Math.cos(dp_xli - g22)         +
                    dp_d3210 * Math.cos( xomi + dp_xli - g32) +
                    dp_d3222 * Math.cos(-xomi + dp_xli - g32) +
                    dp_d5220 * Math.cos( xomi + dp_xli - g52) +
                    dp_d5232 * Math.cos(-xomi + dp_xli - g52) +
                    2.0 * (dp_d4410 * Math.cos(x2omi + x2li - g44) +
                            dp_d4422 * Math.cos(x2li - g44) +
                            dp_d5421 * Math.cos( xomi + x2li - g54) +
                            dp_d5433 * Math.cos(-xomi + x2li - g54));
        }

        pxldot = dp_xni + dp_xfact;
        pxnddt = pxnddt * pxldot;

        return true;
    }

    // ///////////////////////////////////////////////////////////////////////////
    private void deepCalcIntegrator(double pxndot, double pxnddt,
                                    double pxldot, double delta)
    {
        deepCalcDotTerms(pxndot, pxnddt, pxldot);

        dp_xli = dp_xli + pxldot * delta + pxndot * dp_step2;
        dp_xni = dp_xni + pxndot * delta + pxnddt * dp_step2;
        dp_atime = dp_atime + delta;
    }

    // ///////////////////////////////////////////////////////////////////////////
    private boolean deepSecular(double xmdf, double omgadf, double xnode,
                                double emm, double xincc, double xnn,
                                double tsince)
    {
        // Deep space secular effects
        xmdf   = xmdf   + dp_ssl * tsince;
        omgadf = omgadf + dp_ssg * tsince;
        xnode  = xnode  + dp_ssh * tsince;
        emm    = orbit.Eccentricity() + dp_sse * tsince;
        xincc  = orbit.Inclination()  + dp_ssi * tsince;

        if (xincc < 0.0)
        {
            xincc  = -xincc;
            xnode  = xnode  + Globals.PI;
            omgadf = omgadf - Globals.PI;
        }

        double xnddt = 0.0;
        double xndot = 0.0;
        double xldot = 0.0;
        double ft    = 0.0;
        double delt  = 0.0;

        boolean fDone = false;

        if (gp_reso)
        {
            while (!fDone)
            {
                if ((dp_atime == 0.0)                     ||
                        ((tsince >= 0.0) && (dp_atime <  0.0)) ||
                        ((tsince <  0.0) && (dp_atime >= 0.0)))
                {
                    delt =  (tsince < 0) ? dp_stepn : dp_stepp;

                    // Epoch restart
                    dp_atime = 0.0;
                    dp_xni = m_xnodp;
                    dp_xli = dp_xlamo;

                    fDone = true;
                }
                else
                {
                    if (Math.abs(tsince) < Math.abs(dp_atime))
                    {
                        delt = dp_stepp;

                        if (tsince >= 0.0)
                        {
                            delt = dp_stepn;
                        }

                        deepCalcIntegrator(xndot, xnddt, xldot, delt);
                    }
                    else
                    {
                        delt = dp_stepn;

                        if (tsince > 0.0)
                        {
                            delt = dp_stepp;
                        }

                        fDone = true;
                    }
                }
            }

            while (Math.abs(tsince - dp_atime) >= dp_stepp)
            {
                deepCalcIntegrator(xndot, xnddt, xldot, delt);
            }

            ft = tsince - dp_atime;

            deepCalcDotTerms(xndot, xnddt, xldot);

            xnn = dp_xni + xndot * ft + xnddt * ft * ft * 0.5;

            double xl   = dp_xli + xldot * ft + xndot * ft * ft * 0.5;
            double temp = -xnode + dp_thgr + tsince * thdt;

            xmdf = xl - omgadf + temp;

            if (!gp_sync)
            {
                xmdf = xl + temp + temp;
            }
        }

        return true;
    }

    // ///////////////////////////////////////////////////////////////////////////
    private boolean deepPeriodics(double e, double xincc,
                                  double omgadf, double xnode,
                                  double xmam, double tsince)
    {
        // Lunar-solar periodics
        double sinis = Math.sin(xincc);
        double cosis = Math.cos(xincc);

        double sghs = 0.0;
        double shs  = 0.0;
        double sh1  = 0.0;
        double pe   = 0.0;
        double pinc = 0.0;
        double pl   = 0.0;
        double sghl = 0.0;

        double zm = dp_zmos + zns * tsince;
        double zf = zm + 2.0 * zes * Math.sin(zm);
        double sinzf = Math.sin(zf);
        double f2  = 0.5 * sinzf * sinzf - 0.25;
        double f3  = -0.5 * sinzf * Math.cos(zf);
        double ses = dp_se2 * f2 + dp_se3 * f3;
        double sis = dp_si2 * f2 + dp_si3 * f3;
        double sls = dp_sl2 * f2 + dp_sl3 * f3 + dp_sl4 * sinzf;

        sghs = dp_sgh2 * f2 + dp_sgh3 * f3 + dp_sgh4 * sinzf;
        shs = dp_sh2 * f2 + dp_sh3 * f3;
        zm = dp_zmol + znl * tsince;
        zf = zm + 2.0 * zel * Math.sin(zm);
        sinzf = Math.sin(zf);
        f2 = 0.5 * sinzf * sinzf - 0.25;
        f3 = -0.5 * sinzf * Math.cos(zf);

        double sel = dp_ee2 * f2 + dp_e3 * f3;
        double sil = dp_xi2 * f2 + dp_xi3 * f3;
        double sll = dp_xl2 * f2 + dp_xl3 * f3 + dp_xl4 * sinzf;

        sghl = dp_xgh2 * f2 + dp_xgh3 * f3 + dp_xgh4 * sinzf;
        sh1  = dp_xh2 * f2 + dp_xh3 * f3;
        pe   = ses + sel;
        pinc = sis + sil;
        pl   = sls + sll;

        double pgh  = sghs + sghl;
        double ph   = shs + sh1;

        xincc = xincc + pinc;
        e = e + pe;

        if (dp_xqncl >= 0.2)
        {
            // Apply periodics directly
            ph  = ph / m_sinio;
            pgh = pgh - m_cosio * ph;
            omgadf = omgadf + pgh;
            xnode = xnode + ph;
            xmam = xmam + pl;
        }
        else
        {
            // Apply periodics with Lyddane modification
            double sinok = Math.sin(xnode);
            double cosok = Math.cos(xnode);
            double alfdp = sinis * sinok;
            double betdp = sinis * cosok;
            double dalf  =  ph * cosok + pinc * cosis * sinok;
            double dbet  = -ph * sinok + pinc * cosis * cosok;

            alfdp = alfdp + dalf;
            betdp = betdp + dbet;

            double xls = xmam + omgadf + cosis * xnode;
            double dls = pl + pgh - pinc * xnode * sinis;

            xls    = xls + dls;
            xnode  = Globals.acTan(alfdp, betdp);
            xmam   = xmam + pl;
            omgadf = xls - xmam - Math.cos(xincc) * xnode;
        }

        return true;
    }

    /// <summary>
    /// Calculate satellite ECI position/velocity for a given time.
    /// </summary>
    /// <param name="tsince">Target time, in minutes-past-epoch format.</param>
    /// <returns>AU-based position/velocity ECI coordinates.</returns>
    /// <remarks>
    /// This procedure returns the ECI position and velocity for the satellite
    /// in the orbit at the given number of minutes since the TLE epoch time.
    /// The algorithm uses NORAD's Simplified General Perturbation 4 deep space
    /// orbit model.
    /// </remarks>
    @Override
    public EciTime getPosition(double tsince)
    {
        // Update for secular gravity and atmospheric drag
        double xmdf   = orbit.MeanAnomaly() + m_xmdot * tsince;
        double omgadf = orbit.ArgPerigee()  + m_omgdot * tsince;
        double xnoddf = orbit.RAAN() + m_xnodot * tsince;
        double tsq    = tsince * tsince;
        double xnode  = xnoddf + m_xnodcf * tsq;
        double tempa  = 1.0 - m_c1 * tsince;
        double tempe  = orbit.BStar() * m_c4 * tsince;
        double templ  = m_t2cof * tsq;
        double xn     = m_xnodp;
        double em     = 0.0;
        double xinc   = 0.0;

        deepSecular(xmdf, omgadf, xnode, em, xinc, xn, tsince);

        double a    = Math.pow(Globals.Xke / xn, 2.0 / 3.0) * Globals.sqr(tempa);
        double e    = em - tempe;
        double xmam = xmdf + m_xnodp * templ;

        deepPeriodics(e, xinc, omgadf, xnode, xmam, tsince);

        double xl = xmam + omgadf + xnode;

        xn = Globals.Xke / Math.pow(a, 1.5);

        return finalPosition(xinc, omgadf, e, a, xl, xnode, xn, tsince);
    }
}
