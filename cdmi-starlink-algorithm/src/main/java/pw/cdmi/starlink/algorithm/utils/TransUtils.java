package pw.cdmi.starlink.algorithm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import pw.cdmi.starlink.core.Coordinate;
import pw.cdmi.starlink.core.file.LineFileUtils;
import pw.cdmi.starlink.core.orbit.Ecef;
import pw.cdmi.starlink.core.orbit.Geo;
import pw.cdmi.starlink.core.orbit.Vector;

public class TransUtils {
    private final static double DJ00 = 2451545;
    private final static double DJC = 36525;
    private final static double DAS2R = 4.84813681109536e-06;
    private final static double D2PI = 6.28318530717959;
    private final static double DJMJD0 = 2400000.5;
    private final static int TURNAS = 1296000;
    private final static double MJD_J2000 = 51544.5;

    /**
     * 将ECEF坐标按Y向地面上某点坐标与地心线旋转
     */
    public static Ecef ecef2env(double x, double y, double z, double longitude, double latitude) {
        double l_longitude = longitude / 180 * Math.PI;
        double b_latitude = (90 - latitude) / 180 * Math.PI;

        double x1 = Math.cos(b_latitude) * Math.cos(l_longitude) * x + Math.cos(b_latitude) * Math.sin(l_longitude) * y - Math.sin(b_latitude) * z;
        double y1 = -Math.sin(l_longitude) * x + Math.cos(l_longitude) * y;
        double z1 = Math.sin(b_latitude) * Math.cos(l_longitude) * x + Math.sin(b_latitude) * Math.sin(l_longitude) * y + Math.cos(b_latitude) * z;
        Vector v = new Vector();
        v.X = x1;
        v.Y = y1;
        v.Z = z1;
        return new Ecef(v);
    }

    /**
     * 将WGS84坐标转换为ECEF坐标
     */
    public static Ecef WGS842ECEF(double longitude, double latitude, double height) {
        double X;
        double Y;
        double Z;
        double a = 6378137.0;
        double b = 6356752.31424518;
        double E = (a * a - b * b) / (a * a);
        double COSLAT = Math.cos(latitude * Math.PI / 180);
        double SINLAT = Math.sin(latitude * Math.PI / 180);
        double COSLONG = Math.cos(longitude * Math.PI / 180);
        double SINLONG = Math.sin(longitude * Math.PI / 180);
        double N = a / (Math.sqrt(1 - E * SINLAT * SINLAT));
        double NH = N + height;
        X = NH * COSLAT * COSLONG;
        Y = NH * COSLAT * SINLONG;
        Z = (b * b * N / (a * a) + height) * SINLAT;
        Vector v = new Vector();
        v.X = X;
        v.Y = Y;
        v.Z = Z;
        return new Ecef(v);
    }

    /**
     * 将ECEF坐标转换为WGS84坐标
     */
    public static Geo ECEFtoWGS84(double x, double y, double z) {
        double a, b, c, d;
        double Longitude;//经度
        double Latitude;//纬度
        double Altitude;//海拔高度
        double p, q;
        double N;
        a = 6378137.0;
        b = 6356752.31424518;
        c = Math.sqrt(((a * a) - (b * b)) / (a * a));
        d = Math.sqrt(((a * a) - (b * b)) / (b * b));
        p = Math.sqrt((x * x) + (y * y));
        q = Math.atan2((z * a), (p * b));
        Longitude = Math.atan2(y, x);
        Latitude = Math.atan2((z + (d * d) * b * Math.pow(Math.sin(q), 3)), (p - (c * c) * a * Math.pow(Math.cos(q), 3)));
        N = a / Math.sqrt(1 - ((c * c) * Math.pow(Math.sin(Latitude), 2)));
        Altitude = (p / Math.cos(Latitude)) - N;
//        Longitude = Longitude * 180.0 / Math.PI;
//        Latitude = Latitude * 180.0 / Math.PI;
        return new Geo(Longitude, Latitude, Altitude);
    }

    public static double getElevation(double x1, double y1,double z1, double x2, double y2, double z2){
        if(z1 > z2){
            return Math.atan((z1 - z2) / (Math.sqrt(Math.pow((x1 - x2),2) + Math.pow((y1-y2),2))));
        }else{
            return -1.0D;
        }
    }
    
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(2020, 7, 14, 0, 0, 0);
        RealMatrix r_ECI = new Array2DRowRealMatrix(new double[]{2884.07070433846D, -6540.07097854514D, -512.146030678367D,
                -0.795508626691593D,
                -0.918198065656357D, 7.35973309281398D});

        double Mjd_UTC = JulianTimeUtils.Date2Julian(c);
        RealMatrix Y = ECItoECEF(Mjd_UTC, r_ECI);
        System.out.println("数据：" + Y.getData());

    }

    public static RealMatrix ECItoECEF(double mjd_utc, RealMatrix Y0) {
        double x_pole,y_pole,UT1_UTC,LOD,dpsi,deps,dx_pole,dy_pole,TAI_UTC;
        double[] iers_data= IERS(mjd_utc,1);
        x_pole = iers_data[0];
        y_pole = iers_data[1];
        UT1_UTC = iers_data[2];
        LOD = iers_data[3];
        dpsi = iers_data[4];
        deps = iers_data[5];
        dx_pole = iers_data[6];
        dy_pole = iers_data[7];
        TAI_UTC = iers_data[8];

        //执行timediff(UT1_UTC,TAI_UTC)
        double UT1_TAI,UTC_GPS,UT1_GPS,TT_UTC,GPS_UTC;
        double[] timediff_data = timediff(UT1_UTC,TAI_UTC);
        UT1_TAI = timediff_data[0];
        UTC_GPS = timediff_data[1];
        UT1_GPS = timediff_data[2];
        TT_UTC = timediff_data[3];
        GPS_UTC = timediff_data[4];

        Calendar calendar = JulianTimeUtils.Julian2Calendar(mjd_utc + 2400000.5);
        double date = JulianTimeUtils.Date2Julian(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        double time =
                (60 * (60 * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE)) + calendar.get(Calendar.SECOND)) / 86400;
        double UTC = date + time;
        double TT = UTC + TT_UTC / 86400;
        double TUT = time + UT1_UTC / 86400;
        double UT1 = date + TUT;

        //考虑章动影响
        // Form bias-precession-nutation matrix
        double[][] NPB = iauPnm06a(DJMJD0, TT);
        //Form Earth rotation matrix
        double[][] eye = new double[][]{{1,0,0},{0,1,0},{0,0,1}};
        double[][] Theta  = iauRz(iauGst06(DJMJD0, UT1, DJMJD0, TT, NPB),eye);
        //Polar motion matrix (TIRS->ITRS, IERS 2003)
        double[][] Pi = iauPom00(x_pole, y_pole, iauSp00(DJMJD0, TT));

        //ICRS to ITRS transformation matrix and derivative
        double[][] S = new double[][]{{0,0,0,},{0,0,0},{0,0,0}};
        //Derivative of Earth rotation
        S[0][1] = 1;
        S[1][0] = 1;
        double Omega  = 7292115.8553e-11+4.3e-15*( (mjd_utc-MJD_J2000)/36525 ); // [rad/s]
        //Omega = const.omega_Earth-0.843994809*1e-9*LOD;  % IERS
        //double[] dTheta = Omega*S*Theta;           				 //matrix [1/s]
        RealMatrix matrix_S = new Array2DRowRealMatrix(S);
        RealMatrix matrix_Theta = new Array2DRowRealMatrix(Theta);
        RealMatrix dTheta = matrix_S.multiply(matrix_Theta).scalarMultiply(Omega);
        RealMatrix matrix_Pi = new Array2DRowRealMatrix(Pi);
        RealMatrix matrix_NPB = new Array2DRowRealMatrix(NPB);

        RealMatrix U = matrix_Pi.multiply(matrix_Theta).multiply(matrix_NPB);  //ICRS to ITRS transformation
        RealMatrix dU = matrix_Pi.multiply(dTheta).multiply(matrix_NPB);       //Derivative [1/s]
        //Transformation from ICRS to WGS
        RealMatrix r = U.multiply(Y0.getSubMatrix(0,2,0,0));
        RealMatrix v =
                U.multiply(Y0.getSubMatrix(3,5,0,0)).add(dU.multiply(Y0.getSubMatrix(0,2,0,0)));
        RealMatrix Y = r.createMatrix(r.getRowDimension() + v.getRowDimension(),r.getColumnDimension());
        Y.setEntry(0,0, r.getEntry(0,0));
        Y.setEntry(1,0, r.getEntry(1,0));
        Y.setEntry(2,0, r.getEntry(2,0));
        Y.setEntry(3,0, v.getEntry(0,0));
        Y.setEntry(4,0, v.getEntry(1,0));
        Y.setEntry(5,0, v.getEntry(2,0));
        return Y;
    }


    public static double[] timediff(double UT1_UTC,double TAI_UTC){
        double TT_TAI = +32.184;          // TT-TAI time difference [s]
        double GPS_TAI = -19.0;            // GPS-TAI time difference [s]
        double TT_GPS = TT_TAI - GPS_TAI;  // TT-GPS time difference [s]
        double TAI_GPS = -GPS_TAI;         // TAI-GPS time difference [s]
        double UT1_TAI = UT1_UTC - TAI_UTC;  // UT1-TAI time difference [s]
        double UTC_TAI = -TAI_UTC;         // UTC-TAI time difference [s]
        double UTC_GPS = UTC_TAI - GPS_TAI;  // UTC_GPS time difference [s]
        double UT1_GPS = UT1_TAI - GPS_TAI;  // UT1-GPS time difference [s]
        double TT_UTC = TT_TAI - UTC_TAI;   //  TT-UTC time difference [s]
        double GPS_UTC = GPS_TAI - UTC_TAI;  // GPS-UTC time difference [s]
        return new double[]{UT1_TAI,UTC_GPS,UT1_GPS,TT_UTC,GPS_UTC};
    }
    public static double[] IERS(double MJD_UTC, int interp) {
        //加载地球方位参数
        File file = new File(TransUtils.class.getResource("").getPath() + File.separator + "eop19620101.txt");
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String alineString = null;
        String[][] eopdata = new String[21448][13];
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            int i = 0;
            while ((alineString = bufferedReader.readLine()) != null) {
                eopdata[i][0] = alineString.substring(0, 4);
                eopdata[i][1] = alineString.substring(5, 7);
                eopdata[i][2] = alineString.substring(8, 10);
                eopdata[i][3] = alineString.substring(11, 16);
                eopdata[i][4] = alineString.substring(17, 26);
                eopdata[i][5] = alineString.substring(27, 36);
                eopdata[i][6] = alineString.substring(37, 47);
                eopdata[i][7] = alineString.substring(48, 58);
                eopdata[i][8] = alineString.substring(59, 68);
                eopdata[i][9] = alineString.substring(69, 78);
                eopdata[i][10] = alineString.substring(79, 88);
                eopdata[i][11] = alineString.substring(89, 98);
                eopdata[i][12] = alineString.substring(99, 102);
                i++;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader)
                    bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != fileReader)
                    fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //根据IERS时间获取极移数据
        int mjd = (int) Math.floor(MJD_UTC);
        String[] preeop = null;
        String[] nexteop = null;

        for (int i = 0; i < eopdata.length; i++) {
            int tmp_t = Integer.parseInt(eopdata[i][3]);
            if (tmp_t == mjd) {
                preeop = eopdata[i];
                if ((i + 1) < eopdata.length) {
                    nexteop = eopdata[i + 1];
                }
                break;
            }
        }

        int mfme = (int) (1440 * (MJD_UTC - Math.floor(MJD_UTC)));
        int fixf = mfme / 1440;

        double x_pole =
                Double.parseDouble(preeop[4]) + (Double.parseDouble(nexteop[4]) - Double.parseDouble(preeop[4])) * fixf;
        double y_pole =
                Double.parseDouble(preeop[5]) + (Double.parseDouble(nexteop[5]) - Double.parseDouble(preeop[5])) * fixf;
        double UT1_UTC =
                Double.parseDouble(preeop[6]) + (Double.parseDouble(nexteop[6]) - Double.parseDouble(preeop[6])) * fixf;
        double LOD =
                Double.parseDouble(preeop[7]) + (Double.parseDouble(nexteop[7]) - Double.parseDouble(preeop[7])) * fixf;
        double dpsi =
                Double.parseDouble(preeop[8]) + (Double.parseDouble(nexteop[8]) - Double.parseDouble(preeop[8])) * fixf;
        double deps =
                Double.parseDouble(preeop[9]) + (Double.parseDouble(nexteop[9]) - Double.parseDouble(preeop[9])) * fixf;
        double dx_pole =
                Double.parseDouble(preeop[10]) + (Double.parseDouble(nexteop[10]) - Double.parseDouble(preeop[10])) * fixf;
        double dy_pole =
                Double.parseDouble(preeop[11]) + (Double.parseDouble(nexteop[11]) - Double.parseDouble(preeop[11])) * fixf;
        int TAI_UTC = Integer.parseInt(preeop[12].trim());

        final double Arcs = 206264.806247096D;
        x_pole = x_pole / Arcs;
        y_pole = y_pole / Arcs;
        dpsi = dpsi / Arcs;
        deps = deps / Arcs;
        dx_pole = dx_pole / Arcs;
        dy_pole = dy_pole / Arcs;
        return new double[]{x_pole,y_pole,UT1_UTC,LOD,dpsi,deps,dx_pole,dy_pole,TAI_UTC};
    }

    public static double[][] iauPom00(double xp, double yp, double sp){
        //Construct the matrix.
        double[][] rpom = iauIr();
        rpom = iauRz(sp, rpom);
        rpom = iauRy(-xp, rpom);
        rpom = iauRx(-yp, rpom);
        return  rpom;
    }

    public static double[][] iauIr(){
        return new double[][] {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
    }

    public static double[][] iauRx(double phi, double[][] r){
        double s = Math.sin(phi);
        double c = Math.cos(phi);

        double a10 =   c*r[1][0] + s*r[2][0];
        double a11 =   c*r[1][1] + s*r[2][1];
        double a12 =   c*r[1][2] + s*r[2][2];
        double a20 = - s*r[1][0] + c*r[2][0];
        double a21 = - s*r[1][1] + c*r[2][1];
        double a22 = - s*r[1][2] + c*r[2][2];
        r[1][0] = a10;
        r[1][1] = a11;
        r[1][2] = a12;
        r[2][0] = a20;
        r[2][1] = a21;
        r[2][2] = a22;
        return r;
    }

    public static double[][] iauRy(double theta, double[][] r){
        double s = Math.sin(theta);
        double c = Math.cos(theta);

        double a00 =   c*r[0][0] + s*r[2][0];
        double a01 =   c*r[0][1] + s*r[2][1];
        double a02 =   c*r[0][2] + s*r[2][2];
        double a10 = - s*r[0][0] + c*r[2][0];
        double a11 = - s*r[0][1] + c*r[2][1];
        double a12 = - s*r[0][2] + c*r[2][2];
        r[0][0] = a00;
        r[0][1] = a01;
        r[0][2] = a02;
        r[2][0] = a10;
        r[2][1] = a11;
        r[2][2] = a12;
        return r;
    }
    public static double[][] iauRz(double psi, double[][] r){
        double s = Math.sin(psi);
        double c = Math.cos(psi);

        double a00 =   c*r[0][0] + s*r[1][0];
        double a01 =   c*r[0][1] + s*r[1][1];
        double a02 =   c*r[0][2] + s*r[1][2];
        double a10 = - s*r[0][0] + c*r[1][0];
        double a11 = - s*r[0][1] + c*r[1][1];
        double a12 = - s*r[0][2] + c*r[1][2];
        r[0][0] = a00;
        r[0][1] = a01;
        r[0][2] = a02;
        r[1][0] = a10;
        r[1][1] = a11;
        r[1][2] = a12;
        return r;
    }
    public static double iauSp00(double date1, double date2){
        //Interval between fundamental epoch J2000.0 and current date (JC).
        double t = ((date1 - DJ00) + date2) / DJC;
        //Approximate s'.
        double sp = -47e-6 * t * DAS2R;
        return sp;
    }

    public static double iauGst06(double uta, double utb, double tta, double ttb, double[][] rnpb){
        //Extract CIP coordinates.
        double[] x_y = iauBpn2xy(rnpb);
        double x = x_y[0];
        double y = x_y[1];
        //The CIO locator, s.
        double s = iauS06(tta, ttb, x, y);
        //Greenwich apparent sidereal time.
        double era = iauEra00(uta, utb);
        double eors = iauEors(rnpb, s);
        double gst = iauAnp(era - eors);
        return gst;
    }

    public static double iauEors(double[][] rnpb, double s){
        //Evaluate Wallace & Capitaine (2006) expression (16).
        double x = rnpb[2][0];
        double ax = x / (1 + rnpb[2][2]);
        double xs = 1 - ax * x;
        double ys = -ax * rnpb[2][1];
        double zs = -x;
        double p = rnpb[0][0] * xs + rnpb[0][1] * ys + rnpb[0][2] * zs;
        double q = rnpb[1][0] * xs + rnpb[1][1] * ys + rnpb[1][2] * zs;

        double eo;
        if ((p != 0) || (q != 0)) {
            eo = s - Math.atan2(q, p);
        }else{
            eo = s;
        }
        return eo;
    }

    public static double iauEra00(double dj1, double dj2){
        //Days since fundamental epoch.
        double d1;
        double d2;
        if (dj1 < dj2) {
            d1 = dj1;
            d2 = dj2;
        }else {
            d1 = dj2;
            d2 = dj1;
        }

        double t = d1 + (d2- DJ00);

        //Fractional part of T (days).
        double f = mod(d1, 1) + mod(d2, 1);
        //Earth rotation angle at this UT1.
        double theta = iauAnp(D2PI * (f + 0.7790572732640 + 0.00273781191135448 * t));
        return theta;
    }

    private static  double iauAnp(double a){
        double w = mod(a, D2PI);
        if (w < 0) {
            w = w + D2PI;
        }
        return w;
    }

    public static double iauS06(double date1, double date2, double x, double y){
        //Polynomial coefficients: 1-6
        double[] sp = new double[]{94.00e-6, 3808.65e-6, -122.68e-6, -72574.11e-6, 27.98e-6, 15.62e-6};
        //Terms of order t^0
        //l,  l', F,  D, Om,LVe, LE, pA         sine      cosine
        double[][] s0 = new double[][]{
                {0,  0,  0,  0,  1,  0,  0,  0, -2640.73e-6,   0.39e-6},
                {0,  0,  0,  0,  2,  0,  0,  0,   -63.53e-6,   0.02e-6},
                {0,  0,  2, -2,  3,  0,  0,  0,   -11.75e-6,  -0.01e-6},
                {0,  0,  2, -2,  1,  0,  0,  0,   -11.21e-6,  -0.01e-6},
                {0,  0,  2, -2,  2,  0,  0,  0,     4.57e-6,   0.00e-6},
                {0,  0,  2,  0,  3,  0,  0,  0,    -2.02e-6,   0.00e-6},
                {0,  0,  2,  0,  1,  0,  0,  0,    -1.98e-6,   0.00e-6},
                {0,  0,  0,  0,  3,  0,  0,  0,     1.72e-6,   0.00e-6},
                {0,  1,  0,  0,  1,  0,  0,  0,     1.41e-6,   0.01e-6},
                {0,  1,  0,  0, -1,  0,  0,  0,     1.26e-6,   0.01e-6},
                {1,  0,  0,  0, -1,  0,  0,  0,     0.63e-6,   0.00e-6},
                {1,  0,  0,  0,  1,  0,  0,  0,     0.63e-6,   0.00e-6},
                {0,  1,  2, -2,  3,  0,  0,  0,    -0.46e-6,   0.00e-6},
                {0,  1,  2, -2,  1,  0,  0,  0,    -0.45e-6,   0.00e-6},
                {0,  0,  4, -4,  4,  0,  0,  0,    -0.36e-6,   0.00e-6},
                {0,  0,  1, -1,  1, -8, 12,  0,     0.24e-6,   0.12e-6},
                {0,  0,  2,  0,  0,  0,  0,  0,    -0.32e-6,   0.00e-6},
                {0,  0,  2,  0,  2,  0,  0,  0,    -0.28e-6,   0.00e-6},
                {1,  0,  2,  0,  3,  0,  0,  0,    -0.27e-6,   0.00e-6},
                {1,  0,  2,  0,  1,  0,  0,  0,    -0.26e-6,   0.00e-6},
                {0,  0,  2, -2,  0,  0,  0,  0,     0.21e-6,   0.00e-6},
                {0,  1, -2,  2, -3,  0,  0,  0,    -0.19e-6,   0.00e-6},
                {0,  1, -2,  2, -1,  0,  0,  0,    -0.18e-6,   0.00e-6},
                {0,  0,  0,  0,  0,  8,-13, -1,     0.10e-6,  -0.05e-6},
                {0,  0,  0,  2,  0,  0,  0,  0,    -0.15e-6,   0.00e-6},
                {2,  0, -2,  0, -1,  0,  0,  0,     0.14e-6,   0.00e-6},
                {0,  1,  2, -2,  2,  0,  0,  0,     0.14e-6,   0.00e-6},
                {1,  0,  0, -2,  1,  0,  0,  0,    -0.14e-6,   0.00e-6},
                {1,  0,  0, -2, -1,  0,  0,  0,    -0.14e-6,   0.00e-6},
                {0,  0,  4, -2,  4,  0,  0,  0,    -0.13e-6,   0.00e-6},
                {0,  0,  2, -2,  4,  0,  0,  0,     0.11e-6,   0.00e-6},
                {1,  0, -2,  0, -3,  0,  0,  0,    -0.11e-6,   0.00e-6},
                {1,  0, -2,  0, -1,  0,  0,  0,    -0.11e-6,   0.00e-6}
        };

        //Terms of order t^1
        double[][] s1 = new double[][]{
                {0,  0,  0,  0,  2,  0,  0,  0,    -0.07e-6,   3.57e-6},
                {0,  0,  0,  0,  1,  0,  0,  0,     1.73e-6,  -0.03e-6},
                {0,  0,  2, -2,  3,  0,  0,  0,     0.00e-6,   0.48e-6}
        };

        //Terms of order t^2
        double[][] s2 = new double[][]{
                {0,  0,  0,  0,  1,  0,  0,  0,   743.52e-6,  -0.17e-6},
                {0,  0,  2, -2,  2,  0,  0,  0,    56.91e-6,   0.06e-6},
                {0,  0,  2,  0,  2,  0,  0,  0,     9.84e-6,  -0.01e-6},
                {0,  0,  0,  0,  2,  0,  0,  0,    -8.85e-6,   0.01e-6},
                {0,  1,  0,  0,  0,  0,  0,  0,    -6.38e-6,  -0.05e-6},
                {1,  0,  0,  0,  0,  0,  0,  0,    -3.07e-6,   0.00e-6},
                {0,  1,  2, -2,  2,  0,  0,  0,     2.23e-6,   0.00e-6},
                {0,  0,  2,  0,  1,  0,  0,  0,     1.67e-6,   0.00e-6},
                {1,  0,  2,  0,  2,  0,  0,  0,     1.30e-6,   0.00e-6},
                {0,  1, -2,  2, -2,  0,  0,  0,     0.93e-6,   0.00e-6},
                {1,  0,  0, -2,  0,  0,  0,  0,     0.68e-6,   0.00e-6},
                {0,  0,  2, -2,  1,  0,  0,  0,    -0.55e-6,   0.00e-6},
                {1,  0, -2,  0, -2,  0,  0,  0,     0.53e-6,   0.00e-6},
                {0,  0,  0,  2,  0,  0,  0,  0,    -0.27e-6,   0.00e-6},
                {1,  0,  0,  0,  1,  0,  0,  0,    -0.27e-6,   0.00e-6},
                {1,  0, -2, -2, -2,  0,  0,  0,    -0.26e-6,   0.00e-6},
                {1,  0,  0,  0, -1,  0,  0,  0,    -0.25e-6,   0.00e-6},
                {1,  0,  2,  0,  1,  0,  0,  0,     0.22e-6,   0.00e-6},
                {2,  0,  0, -2,  0,  0,  0,  0,    -0.21e-6,   0.00e-6},
                {2,  0, -2,  0, -1,  0,  0,  0,     0.20e-6,   0.00e-6},
                {0,  0,  2,  2,  2,  0,  0,  0,     0.17e-6,   0.00e-6},
                {2,  0,  2,  0,  2,  0,  0,  0,     0.13e-6,   0.00e-6},
                {2,  0,  0,  0,  0,  0,  0,  0,    -0.13e-6,   0.00e-6},
                {1,  0,  2, -2,  2,  0,  0,  0,    -0.12e-6,   0.00e-6},
                {0,  0,  2,  0,  0,  0,  0,  0,    -0.11e-6,   0.00e-6}
        };
        //Terms of order t^3
        double[][] s3 = new double[][]{
                {0,  0,  0,  0,  1,  0,  0,  0,     0.30e-6, -23.42e-6},
                {0,  0,  2, -2,  2,  0,  0,  0,    -0.03e-6,  -1.46e-6},
                {0,  0,  2,  0,  2,  0,  0,  0,    -0.01e-6,  -0.25e-6},
                {0,  0,  0,  0,  2,  0,  0,  0,     0.00e-6,   0.23e-6}
        };

        //Terms of order t^4
        double[][] s4 = new double[][]{
                {0,  0,  0,  0,  1,  0,  0,  0,    -0.26e-6,  -0.01e-6}
        };

        double t = ((date1 - DJ00) + date2) / DJC;
        //Mean anomaly of the Moon.
        double[] fa = new double[8];
        fa[0] =  mod( 485868.249036 +
        t * ( 1717915923.2178 +
        t * (         31.8792 +
        t * (          0.051635 +
        t * (        - 0.00024470 ) ) ) ), TURNAS ) * DAS2R;
        //Mean anomaly of the Sun.
        fa[1] = mod( 1287104.793048 +
        t * ( 129596581.0481 +
        t * (       - 0.5532 +
        t * (         0.000136 +
        t * (       - 0.00001149 ) ) ) ), TURNAS ) * DAS2R;
        //Mean longitude of the Moon minus that of the ascending node.
        fa[2] = mod( 335779.526232 +
        t * ( 1739527262.8478 +
        t * (       - 12.7512 +
        t * (        - 0.001037 +
        t * (          0.00000417 ) ) ) ), TURNAS ) * DAS2R;
        //Mean elongation of the Moon from the Sun.
        fa[3] = mod(    1072260.703692 +
        t * ( 1602961601.2090 +
        t * (        - 6.3706 +
        t * (          0.006593 +
        t * (        - 0.00003169 ) ) ) ), TURNAS ) * DAS2R;
        //Mean longitude of the ascending node of the Moon.
        fa[4] = mod(       450160.398036 +
        t * ( - 6962890.5431 +
        t * (         7.4722 +
        t * (       0.007702 +
        t * (     - 0.00005939 ) ) ) ), TURNAS ) * DAS2R;
        //Mean longitude of Venus.
        fa[5] = mod(3.176146697 + 1021.3285546211 * t, D2PI);
        //Mean longitude of Earth.
        fa[6] = mod(1.753470314 + 628.3075849991 * t, D2PI);
        //General precession in longitude.
        fa[7] = (0.024381750 + 0.00000538691 * t) * t;

        //Evaluate s.
        double w0 = sp[0];
        double w1 = sp[1];
        double w2 = sp[2];
        double w3 = sp[3];
        double w4 = sp[4];
        double w5 = sp[5];

        for(int i = s0.length;i>0;i--){
            double a =0;
            for(int j =0; j<8;j++){
                a = a + s0[i-1][j] * fa[j];
            }
            w0 = w0 + s0[i-1][8] * Math.sin(a) + s0[i-1][9] * Math.cos(a);
        }

        for(int i = s1.length;i>0;i--){
            double a =0;
            for(int j =0; j<8;j++){
                a = a + s1[i-1][j] * fa[j];
            }
            w1 = w1 + s1[i-1][8] * Math.sin(a) + s1[i-1][9] * Math.cos(a);
        }

        for(int i = s2.length;i>0;i--){
            double a =0;
            for(int j =0; j<8;j++){
                a = a + s2[i-1][j] * fa[j];
            }
            w2 = w2 + s2[i-1][8] * Math.sin(a) + s2[i-1][9] * Math.cos(a);
        }

        for(int i = s3.length;i>0;i--){
            double a =0;
            for(int j =0; j<8;j++){
                a = a + s3[i-1][j] * fa[j];
            }
            w3 = w3 + s3[i-1][8] * Math.sin(a) + s3[i-1][9] * Math.cos(a);
        }

        for(int i = s4.length;i>0;i--){
            double a =0;
            for(int j =0; j<8;j++){
                a = a + s4[i-1][j] * fa[j];
            }
            w4 = w4 + s4[i-1][8] * Math.sin(a) + s4[i-1][9] * Math.cos(a);
        }
        double s = (w0 + (w1 + (w2 + (w3 + (w4 + w5 * t) * t) * t) * t) * t) * DAS2R - x*y/2.0;
        return s;
    }

    //Extract CIP coordinates.
    public static double[] iauBpn2xy(double[][] rbpn){
        //Extract the X,Y coordinates.
        double x = rbpn[2][0];
        double y = rbpn[2][1];
        return new double[]{x,y};
    }

    public static double[][] iauPnm06a(double date1, double date2) {
        //Fukushima-Williams angles for frame bias and precession
        double[] gamb_phib_psib_epsa = iauPfw06(date1, date2);
        double gamb = gamb_phib_psib_epsa[0];
        double phib = gamb_phib_psib_epsa[1];
        double psib = gamb_phib_psib_epsa[2];
        double epsa = gamb_phib_psib_epsa[3];
        //Nutation components
        double[] dp_de = iauNut06a(date1, date2);
        double dp = dp_de[0];
        double de = dp_de[1];
        //Equinox based nutation x precession x bias matrix
        double[][] rnpb = iauFw2m(gamb, phib, psib + dp, epsa + de);
        return rnpb;
    }

    public static double[][] iauFw2m(double gamb, double phib, double psi, double eps) {
        double[][] r = iauIr();
        r = iauRz(gamb,r);
        r = iauRx(phib, r);
        r = iauRz(-psi, r);
        r = iauRx(-eps, r);
        return r;
    }

    public static double[] iauPfw06(double date1, double date2) {
        // Interval between fundamental date J2000.0 and given date (JC).
        double t = ((date1 - DJ00) + date2) / DJC;

        // P03 bias+precession angles
        double gamb =
                (-0.052928 + (10.556378 + (0.4932044 + (-0.00031238 + (-0.000002788 + (0.0000000260) * t) * t) * t) * t) * t) * DAS2R;
        double phib = (84381.412819 + (-46.811016 + (0.0511268 + (0.00053289 + (-0.000000440 + (-0.0000000176) * t) * t) * t) * t) * t) * DAS2R;
        double psib = (-0.041775 +
                (5038.481484 +
                        (1.5584175 +
                                (-0.00018522 +
                                        (-0.000026452 +
                                                (-0.0000000148)
                                                        * t) * t) * t) * t) * t) * DAS2R;
        // Mean obliquity.
        double epsa = (84381.406 +
                (-46.836769 +
                        (-0.0001831 +
                                (0.00200340 +
                                        (-0.000000576 +
                                                (-0.0000000434) * t) * t) * t) * t) * t) * DAS2R;
        return new double[]{gamb, phib, psib, epsa};
    }

    public static double[] iauNut06a(double date1, double date2) {
        //Interval between fundamental date J2000.0 and given date (JC).
        double t = ((date1 - DJ00) + date2) / DJC;
        // Factor correcting for secular variation of J2.
        double fj2 = -2.7774e-6 * t;
        //Obtain IAU 2000A nutation.
        double[] dp_de = iauNut00a(date1, date2);

        double dp = dp_de[0];
        double de = dp_de[1];

        //Apply P03 adjustments (Wallace & Capitaine, 2006, Eqs.5).
        double dpsi = dp + dp * (0.4697e-6 + fj2);
        double deps = de + de * fj2;
        return new double[]{dpsi, deps};
    }

    public static double[] iauNut00a(double date1, double date2) {
        double t = ((date1 - DJ00) + date2) / DJC;
        double U2R = DAS2R / 1e7;

        //Mean anomaly of the Moon (IERS 2003).
        double el = mod(485868.249036 +
                (1717915923.2178 +
                        (31.8792 +
                                (0.051635 +
                                        (-0.00024470) * t) * t) * t) * t, TURNAS) * DAS2R;

        //Mean anomaly of the Sun (MHB2000)
        double elp = mod(1287104.79305 +
                (129596581.0481 +
                        (-0.5532 +
                                (0.000136 +
                                        (-0.00001149) * t) * t) * t) * t, TURNAS) * DAS2R;

        //Mean longitude of the Moon minus that of the ascending node (IERS 2003)
        double f = mod(335779.526232 +
                t * (1739527262.8478 +
                        t * (-12.7512 +
                                t * (-0.001037 +
                                        t * (0.00000417)))), TURNAS) * DAS2R;

        //Mean elongation of the Moon from the Sun (MHB2000).
        double d = mod(1072260.70369 +
                t * (1602961601.2090 +
                        t * (-6.3706 +
                                t * (0.006593 +
                                        t * (-0.00003169)))), TURNAS) * DAS2R;

        //Mean longitude of the ascending node of the Moon (IERS 2003).
        double om = mod(450160.398036 +
                t * (-6962890.5431 +
                        t * (7.4722 +
                                t * (0.007702 +
                                        t * (-0.00005939)))), TURNAS) * DAS2R;
        //Initialize the nutation values.
        double dp = 0.0;
        double de = 0.0;

        String xls_path = TransUtils.class.getResource("").getPath() + File.separator + "iauNut00a_xls.txt";
        double[][] xls = LineFileUtils.readLine4Double(xls_path, " ");

        for (int i = xls.length; i < 0; i--) {
            double arg =
                    mod(xls[i][1] * el + xls[i][2] * elp + xls[i][3] * f + xls[i][4] * d + xls[i][5] * om, D2PI);
            double sarg = Math.sin(arg);
            double carg = Math.cos(arg);
            dp = dp + (xls[i][6] + xls[i][7] * t) * sarg + xls[i][8] * carg;
            de = de + (xls[i][9] + xls[i][10] * t) * carg + xls[i][11] * sarg;
        }

        //Convert from 0.1 microarcsec units to radians.
        double dpsils = dp * U2R;
        double depsls = de * U2R;

        //Mean anomaly of the Moon (MHB2000).
        double al = mod(2.35555598 + 8328.6914269554 * t, D2PI);
        //Mean longitude of the Moon minus that of the ascending node (MHB2000).
        double af = mod(1.627905234 + 8433.466158131 * t, D2PI);
        //Mean elongation of the Moon from the Sun (MHB2000)
        double ad = mod(5.198466741 + 7771.3771468121 * t, D2PI);
        //Mean longitude of the ascending node of the Moon (MHB2000)
        double aom = mod(2.18243920 - 33.757045 * t, D2PI);
        //General accumulated precession in longitude (IERS 2003)
        double apa = (0.024381750 + 0.00000538691 * t) * t;
        //Mean longitude of Mercury (IERS Conventions 2003)
        double alme = mod(4.402608842 + 2608.7903141574 * t, D2PI);
        //Mean longitude of Venus (IERS Conventions 2003)
        double alve = mod(3.176146697 + 1021.3285546211 * t, D2PI);
        //Mean longitude of Earth (IERS Conventions 2003)
        double alea = mod(1.753470314 + 628.3075849991 * t, D2PI);
        //Mean longitude of Mars (IERS Conventions 2003)
        double alma = mod(6.203480913 + 334.0612426700 * t, D2PI);
        //Mean longitude of Jupiter (IERS Conventions 2003)
        double alju = mod(0.599546497 + 52.9690962641 * t, D2PI);
        //Mean longitude of Saturn (IERS Conventions 2003)
        double alsa = mod(0.874016757 + 21.3299104960 * t, D2PI);
        //Mean longitude of Uranus (IERS Conventions 2003)
        double alur = mod(5.481293872 + 7.4781598567 * t, D2PI);
        //Neptune longitude (MHB2000).
        double alne = mod(5.321159000 + 3.8127774000 * t, D2PI);

        //Initialize the nutation values.
        dp = 0.0;
        de = 0.0;

        String xpl_path = TransUtils.class.getResource("").getPath() + File.separator + "iauNut00a_xpl.txt";
        double[][] xpl = LineFileUtils.readLine4Double(xls_path, " ");
        for (int i = xpl.length; i < 0; i--) {
            double arg = mod(xpl[i][1] * al + xpl[i][2] * af + xpl[i][3] * ad +
                    xpl[i][4] * aom + xpl[i][5] * alme + xpl[i][6] * alve +
                    xpl[i][7] * alea + xpl[i][8] * alma + xpl[i][9] * alju +
                    xpl[i][10] * alsa + xpl[i][11] * alur + xpl[i][12] * alne +
                    xpl[i][13] * apa, D2PI);
            double sarg = Math.sin(arg);
            double carg = Math.cos(arg);

            dp = dp + xpl[i][14] * sarg + xpl[i][15] * carg;
            de = de + xpl[i][16] * sarg + xpl[i][17] * carg;
        }

        double dpsipl = dp * U2R;
        double depspl = de * U2R;

        //Add luni-solar and planetary components.
        double dpsi = dpsils + dpsipl;
        double deps = depsls + depspl;
        return new double[]{dpsi, deps};
    }

    /**
     * IAU 2006岁差模型
     * Mean obliquity of the ecliptic, IAU 2006 precession model.
     *
     * @param date1
     * @param date2
     * @return
     */
    public static double iauObl06(double date1, double date2) {
        // Interval between fundamental date J2000.0 and given date (JC).
        final double DJ00 = 2451545;
        final double DJC = 36525;
        final double DAS2R = 4.84813681109536e-06;

        double t = ((date1 - DJ00) + date2) / DJC;

        // Mean obliquity.
        double eps0 = (84381.406 +
                (-46.836769 +
                        (-0.0001831 +
                                (0.00200340 +
                                        (-0.000000576 +
                                                (-0.0000000434) * t) * t) * t) * t) * t) * DAS2R;
        return eps0;
    }

    private static double mod(double x, double y) {
        return x - Math.floor(x / y) * y;
    }
}
