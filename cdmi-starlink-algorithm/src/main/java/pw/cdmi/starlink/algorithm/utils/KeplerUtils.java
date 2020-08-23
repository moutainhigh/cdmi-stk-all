package pw.cdmi.starlink.algorithm.utils;

/**
 * 根据开普勒定理进行的相关计算
 */
public class KeplerUtils {
    private static final double Re = 6378.137;                      //地球平均半径，单位：公里
    private static final double Gu = 398601.58;                     //开普勒常数，单位 KM3/S2 与引力常数有差异Globals.Gm=398601.2,Globals.Ge=398600.8(WGS '72)
    private static final double Te = 86164;                         //1个恒星日（23小时56分04秒），单位：秒
    private static final double We = 7.292 * Math.pow(10, -5);      //地球自转角速度，平均数为7.292×10^（-5）弧度/秒

    public static void main(String[] args) {
        //方法测试，某卫星，近地点高度(到地面)为1000km，远地点高度为4000km，求卫星的轨道周期
        //1.  轨道长半轴
        double a = (2 * Re + 1000 + 4000) / 2;
        System.out.println("轨道周期：  " + KeplerUtils.computerMotionPeriod(a));
        double t_e = 1 - ((1000 + Re) / a);
        System.out.println("轨道偏心率：" + t_e);
        //偏心率为0.7033,求轨道半焦距
        double e = t_e;
//        double e = 0.7033;
        System.out.println("轨道半焦距：" + KeplerUtils.computerHalfFocalLength(a, e));
        double t_a = KeplerUtils.computerHalfFocalLength(a, e) + Re + 1000;
        System.out.println("验证长半轴: " + t_a);
        System.out.println("轨道长半轴: " + a);
        System.out.println("卫星远地点速度: " + KeplerUtils.computerApogeeVelocity(a, e));
        System.out.println("卫星近地点速度: " + KeplerUtils.computerPerigeeVelocity(a, e));

        double E = -10; //单位度
        double h = 36000;//单位公里 ，500~36000
        System.out.println("卫星到观察点的距离: " + KeplerUtils.computerDistanceOfSatellite2ObservationPointByElevation(h, E));
        h = 1450;
        System.out.println("卫星和观察点间的地心角： " + KeplerUtils.computerGeocentricAngleByElevation(h, E));
        System.out.println("卫星在轨运动角速度：" + KeplerUtils.computerAngularVelocityOfSatel(h));
        System.out.println("最长连续服务时间：" + KeplerUtils.computerServicetime(h, E));

        KeplerUtils.computerMeanAnomaly(1999, 30, 0);

        a= 1045;
        double i = 025.0191; //轨道倾角
        double Ω = 358.9828; //升交点赤经
        e= 0.7597678; //轨道偏心率
        double omega = 197.8808;   //近地点幅角
        double M = 102.7839;   //平近点角
        //首先计算卫星的轨道周期
        double T = 2 * Math.PI * Math.sqrt(Math.pow(a,3)/Gu);
    }
    //*********************开普勒第一定理*****************************//

    /**
     * 计算偏心率
     *
     * @param a 轨道长半轴
     * @param b 轨道短半轴
     * @return
     */
    public static double computerEccentricity(double a, double b) {
        double e = Math.sqrt(1 - Math.pow((b / a), 2));
        return e;
    }

    /**
     * 计算半焦距长度
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     * @return
     */
    public static double computerHalfFocalLength(double a, double e) {
        double Rh = a * e;
        return Rh;
    }

    /**
     * 计算远地点
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     */
    public static double computerApogee(double a, double e) {
        double Ra = a * (1 + e);
        return Ra;
    }

    /**
     * 计算近地点
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     */
    public static double computerPerigee(double a, double e) {
        double Rp = a * (1 - e);
        return Rp;
    }

    /**
     * 计算卫星在卫星轨道平面上的极坐标
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     * @param o 瞬时卫星-地心连线与地心-近地点连线的夹角，是卫星在轨道面内相对于近地点的相位偏移量
     * @return r 为瞬时卫星到地心的距离
     */
    public static double computerSal2GeocentricDistance(double a, double e, double o) {
        double p = a * (1 - Math.pow(e, 2));         //p被称呼为椭圆半焦弦
        double r = p / (e * Math.cos(o));
        return r;
    }

    //*********************开普勒第二定理*****************************//

    /**
     * 计算椭圆轨道上的卫星的瞬时速度,单位（km/s)
     *
     * @param a 轨道长半轴
     * @param r 为瞬时卫星到地心的距离
     * @return
     */
    public static double computerVelocityByGeocentricDistance(double a, double r) {
        double V = Math.sqrt(Gu * (2 / r - 1 / a));
        return V;
    }

    /**
     * 计算椭圆轨道上的卫星的瞬时速度,单位（km/s)
     * 应该是不正确的
     * @param a 轨道长半轴
     * @param T 轨道周期
     * @return
     */
    public static double computerVelocityByOrbitalPeriod(double a, double T) {
        double Vs = 2 * Math.PI * a / T;
        return Vs;
    }

    /**
     * 计算椭圆轨道上的卫星的瞬时速度,单位（km/s)
     * @param a 轨道长半轴
     * @return
     */
    public static double computerVelocityByOrbitalPeriod(double a) {
        double Vs = Math.sqrt(Gu / a);
        return Vs;
    }

    /**
     * 计算卫星在远地点时的速度
     * 验证正确
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     * @return
     */
    public static double computerApogeeVelocity(double a, double e) {
        double Va = Math.sqrt((Gu / a) * ((1 - e) / (1 + e)));
        return Va;
    }

    /**
     * 计算卫星在近地点时的速度
     * 验证正确
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     * @return
     */
    public static double computerPerigeeVelocity(double a, double e) {
        double Vp = Math.sqrt((Gu / a) * ((1 + e) / (1 - e)));
        return Vp;
    }

    //*********************开普勒第三定理*****************************//

    /**
     * 获得卫星围绕地球飞行的周期，单位为s
     * 测试正确
     *
     * @param a 轨道长半轴
     * @return
     */
    public static double computerMotionPeriod(double a) {
        double T = 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / Gu);
        return T;
    }

    /**
     * 计算卫星离开近地点后t时间内平均近点角
     *
     * @param Ts 卫星的轨道周期
     * @param t  卫星离开近地点后的时间
     * @param tp 卫星经过近地点的时间
     * @return
     */
    public static double computerMeanAnomalyByTime(double Ts, double t, double tp) {
        double M = (2 * Math.PI / Ts) * (t - tp);
        return M;
    }

    /**
     * 卫星星下点在M个恒星日后，围绕地球旋转N圈后重复轨道的回归周/准回归轨道的周期
     *
     * @param M 恒星日数量
     * @param N 围绕地球旋转的圈数
     * @return
     */
    public static double computerPeriodOfReturnOrbit(double M, double N) {
        double Ts = Te * (M / N);
        return Ts;
    }

    //*********************卫星的定位*****************************//

    /**
     * 根据开普勒第二定理，推导偏心率近点角与平均近点角M之间的关系（我靠，怎么推导的？）
     * 该方程式被称为开普勒方式
     *
     * @param E 偏心近点角（Eccentric Anomaly）
     * @param e 轨道偏心率
     * @param M 平均近点角M
     * @return
     */
    public static double computerMeanAnomaly(double E, double e, double M) {
        double Mk = E - e * Math.sin(E);
        double Ek = E + (M - Mk) / (1 - e * Math.sin(E));
        double epsilon = 0.01;  //允许的最大误差
        if ((Mk - M) < epsilon) {
            return Mk;
        } else {
            System.out.println(Mk);
            computerMeanAnomaly(E, e, Mk);
        }
        return Mk;
    }

    /**
     * 根据瞬时偏心近点角E，利用高斯方程计算真近点角
     *
     * @param E 偏心近点角（Eccentric Anomaly）E可采用牛顿迭代法和线性迭代法来计算E的值
     * @param e 轨道偏心率
     * @return
     */
    public static double computerTrueAnomaly(double E, double e) {
        double o = E - 2 * Math.toDegrees(Math.atan(Math.sqrt((1 + e) / (1 - e)) * Math.tan(E / 2)));
        return o;
    }

    /**
     * 计算卫星在卫星轨道平面上的极坐标（卫星到地心的距离）
     *
     * @param a 轨道长半轴
     * @param e 轨道偏心率
     * @param E 偏心近点角(Eccentric Anomaly)
     * @return r 为瞬时卫星到地心的距离
     */
    public static double computerSal2GeocentricDistanceByEccentricAnomaly(double a, double e, double E) {
        double r = a * (1 - e * Math.cos(E));
        return r;
    }

    /**
     * 计算平均轨道速率
     *
     * @param T 轨道周期
     * @return
     */
    public static double computerMeanOrbitalVelocity(double T) {
        double n = 2 * Math.PI / T;
        return n;
    }

    /**
     * 计算t时刻的平均近点角
     *
     * @param Mo 初始平均近点角
     * @param n  平均轨道速率
     * @param t  t时刻
     * @return
     */
    public static double computerMeanAnomalyByMeanOrbitalVelocity(double Mo, double n, double t) {
        double M = Mo + n * t;
        return M;
    }

    //*********************星下点轨迹*****************************//

    /**
     * 计算卫星在任意时刻t(t>0)时的星下点经度
     *
     * @param Ro 生交点经度
     * @param i  轨道倾角
     * @param o  是t时刻卫星在轨道平面内相对于右升交点的角距，单位 度
     * @param t  时刻
     * @return
     */
    public static double computerLongitude(double Ro, double i, double o, long t, boolean direction) {
        double t_o = 0;
        if (o >= -180 && o < -90) {
            t_o = -180;
        } else if (o >= -90 && o <= 90) {
            t_o = 0;
        } else {
            t_o = 180;
        }

        if (!direction) {
            t_o = -t_o;
        }
        double Rs = Ro + Math.atan(Math.cos(i) * Math.tan(o) - We * t + t_o);
        return Rs;
    }

    /**
     * 计算卫星的星下点经度
     *
     * @param i
     * @param o
     * @return
     */
    public static double computerLatitude(double i, double o) {
        double Qs = Math.asin(Math.sin(i) * Math.sin(o));
        return Qs;
    }

    /***********************卫星覆盖计算***************************/
    /**
     * 卫星和观察点间的地心角
     *
     * @param h 卫星轨道高度
     * @param E 观察点对卫星的仰角，以观察点的地平线为参考，取值范围[-90,90],单位 度（°）
     * @return
     */
    public static double computerGeocentricAngleByElevation(double h, double E) {
        double alpha = Math.acos((Re / (h + Re)) * Math.cos(E)) - E;
        return alpha;
    }

    /**
     * 卫星和观察点间的地心角
     *
     * @param h    卫星轨道高度
     * @param beta 卫星的半视角（半俯角），取值范围[0-90],单位度
     * @return
     */
    public static double computerGeocentricAngleByHalfViewAngle(double h, double beta) {
        double alpha = Math.asin((h + Re) / Re * Math.sin(beta)) - beta;
        return alpha;
    }

    /**
     * 卫星和观察点间的地心角
     *
     * @param u_longitude 观察点的瞬时经度
     * @param u_latitude  观察点的瞬时纬度
     * @param s_longitude 卫星瞬时经度
     * @param s_latitude  卫星瞬时纬度
     * @return
     */
    public static double computerGeocentricAngle(double u_longitude, double u_latitude, double s_longitude, double s_latitude) {
        double alpha = Math.acos((Math.sin(u_latitude) * Math.sin(s_latitude) + Math.cos(u_latitude) * Math.cos(s_latitude) * Math.cos(u_longitude - s_longitude)));
        return alpha;
    }

    /**
     * 卫星的半视角（半俯角）
     *
     * @param h 卫星轨道高度
     * @param E 观察点对卫星的仰角，以观察点的地平线为参考，取值范围[-90,90],单位 度（°）
     * @return
     */
    public static double computerHalfViewAngleByElevation(double h, double E) {
        double beta = Math.asin(Re / (h + Re) * Math.cos(E));
        return beta;
    }

    /**
     * 卫星的半视角（半俯角）
     *
     * @param h     卫星轨道高度
     * @param alpha 卫星和观察点间的地心角
     * @return
     */
    public static double computerHalfViewAngleByGeocentricAngle(double h, double alpha) {
        double beta = Math.atan((Re * Math.sin(alpha) / (h + Re - Re * Math.cos(alpha))));
        return beta;
    }

    /**
     * 获得观察点的仰角
     *
     * @param h     卫星轨道高度
     * @param alpha 卫星和观察点间的地心角
     * @return
     */
    public static double computerElevationByGeocentricAngle(double h, double alpha) {
        double E = Math.atan(((h + Re) * Math.cos(alpha) - Re) / ((h + Re) * Math.sin(alpha)));
        return E;
    }

    /**
     * 获得观察点的仰角
     *
     * @param h    卫星轨道高度
     * @param beta 卫星的半视角（半俯角）
     * @return
     */
    public static double computerElevationByHalfViewAngle(double h, double beta) {
        double E = Math.acos((h + Re) / Re * Math.sin(beta));
        return E;
    }

    /**
     * 获得星到观测点的距离
     *
     * @param h     卫星轨道高度
     * @param alpha 卫星与观察点间的地心角
     * @return
     */
    public static double computerDistanceOfSatellite2ObservationPoint(double h, double alpha) {
        double d = Math.sqrt(Math.pow(Re, 2) + Math.pow((h + Re), 2) - 2 * Re * (h + Re) * Math.cos(alpha));
        return d;
    }

    /**
     * 获得星到观测点的距离
     *
     * @param h 卫星轨道高度
     * @param E 观察点对卫星的仰角
     * @return
     */
    public static double computerDistanceOfSatellite2ObservationPointByElevation(double h, double E) {
        double d = Math.sqrt(Math.pow(Re, 2) * Math.pow(Math.sin(E), 2) + 2 * h * Re + Math.pow(h, 2)) - Re * Math.sin(E);
        return d;
    }

    /**
     * 计算覆盖区半径
     *
     * @param alpha 卫星与观察点间的地心角
     */
    public static double computerRadiusOfCoverageArea(double alpha) {
        double X = Re * Math.sin(alpha);
        return X;
    }

    /**
     * 计算覆盖区面积
     *
     * @param alpha 卫星与观察点间的地心角
     */
    public static double computerAreaOfCoverage(double alpha) {
        double A = 2 * Math.PI * Math.pow(Re, 2) * (1 - Math.cos(alpha));
        return A;
    }

    /**
     * 计算卫星的角速度
     *
     * @param Ts 卫星的轨道周期
     * @return
     */
    public static double computerAngularVelocityOfSatelByMeanAnomaly(double Ts) {
        double Ws = 2 * Math.PI / Ts;
        return Ws;
    }

    /**
     * 计算卫星的角速度
     *
     * @param h 卫星的轨道高度
     * @return
     */
    public static double computerAngularVelocityOfSatel(double h) {
        double Ws = Math.sqrt(Gu / Math.pow((h + Re), 3));
        return Math.toDegrees(Ws);
    }

    /**
     * 获得最长连续服务时间
     *
     * @param h     卫星的轨道高度
     * @param E_min 观察点的最小仰角
     * @return
     */
    public static double computerServicetime(double h, double E_min) {
        double Ws = KeplerUtils.computerAngularVelocityOfSatel(h);
        double alpha_max = KeplerUtils.computerGeocentricAngleByElevation(h, E_min);
        double t_max = 2 * alpha_max / Ws;
        return t_max;
    }

    /***********************卫星轨道摄动***************************/
    /***地球扁平度影响***/
    /**
     * 计算卫星轨道在升交点沿赤道漂移量，单位 度/天
     *
     * @param T
     * @param α
     * @param J2
     * @param e
     * @param i
     * @return
     */
    public static double computerDriftAlongEquatorOfRisingPoint(double T, double α, double J2, double e, double i) {
        double Ω =
                -3 / 2 * (2 * Math.PI / T) * Math.pow((Re / α), 2) * ((J2 / Math.pow((1 - Math.pow(e, 2)), 2)) * Math.cos(i));
        return Ω;
    }

    /**
     * 计算卫星轨道在升交点沿赤道漂移量，单位 度/天
     *
     * @param α
     * @param e
     * @param i
     * @return
     */
    public static double computerDriftAlongEquatorOfRisingPoint(double α, double e, double i) {
        double Ω = -9.964 / Math.pow((1 - Math.pow(e, 2)), 2) * Math.pow((Re / α), 3.5) * Math.cos(i);
        return Ω;
    }

    /**
     * 计算轨道近地点幅角在轨道面内向前或向后的旋转速度
     *
     * @param α
     * @param e
     * @param i
     * @return
     */
    public static double computerRotationSpeedOfPerigeeAngle(double T, double α, double J2, double e, double i) {
        double w =
                -3 / 4 * (2 * Math.PI / T) * Math.pow((Re / α), 2) * (J2 / Math.pow((1 - Math.pow(e, 2)),2)) * (5 * Math.pow(Math.cos(i), 2));
        return w;
    }

    /**
     * 计算轨道近地点幅角在轨道面内向前或向后的旋转速度
     *
     * @param α
     * @param e
     * @param i
     * @return
     */
    public static double computerRotationSpeedOfPerigeeAngle(double α, double e, double i) {
        double w = -4.982 / Math.pow((1 - Math.pow(e, 2)), 2) * Math.pow((Re / α), 3.5) * (5 * Math.pow(Math.cos(i), 2) - 1);
        return w;
    }

    /**
     * 太阳，月亮对轨道倾角的影响（度/年）
     * @param Ω 月球轨道在黄道面内的右旋升交点赤经
     */
    public static double computerInclination(double Ω){
        double A = 0.8457;
        double B = 0.0981;
        double C = -0.090;
        double total_d = Math.sqrt(Math.pow((A + B * Math.cos(Ω)),2) + (C * Math.sin(Ω)));
        return total_d;
    }

    /**
     * 月球轨道在黄道面内的右旋升交点赤经
     * @param T 以年为单位表示的时期
     * @return
     */
    public static double computerRAANOfMoon(double T){
        double Ω = - 2 * Math.PI / 18.613 * (T-1969.244);
        return Ω;
    }

    /**
     * 计算卫星与接收机（观察点）间的径向速度
     * @param a 卫星的长半轴
     * @param theta 观察点与卫星间的地心夹角
     * @return
     */
    public static double computerRadialVelocity(double a,  double theta){
        double Vs = computerVelocityByOrbitalPeriod(a);
        double Vt = Vs * Math.cos(theta);
        return Vt;
    }
}
