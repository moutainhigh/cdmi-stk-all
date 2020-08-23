package pw.cdmi.starlink.algorithm.utils;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import pw.cdmi.starlink.core.orbit.Globals;

public class OrbitUtils {

    private final static double K = 398601.58;

    public static void main(String[] args) {
        double a = 7471.072;
        double e = 0.001365;
        double i = Globals.toRadians(49.995);
        double Ω = Globals.toRadians(356.24);
        double ω = Globals.toRadians(75.033);
        double M = Globals.toRadians(40.139);
        OrbitUtils.computerOrbit(a, e, i, Ω, ω, M);
    }

    /**
     * 通过卫星瞬根数据获取卫星的瞬时ECI位置
     *
     * @param a 轨道长半轴 KM
     * @param e 轨道偏心率
     * @param i 轨道倾角
     * @param Ω 轨道升交点赤经
     * @param ω 近地点幅角
     * @param M 平近点角
     * @return
     */
    public static void computerOrbit(double a, double e, double i, double Ω, double ω, double M) {
        RealMatrix A1 = MatrixUtils.createRealMatrix(3, 3);
        A1.setRow(0, new double[]{Math.cos(-ω), Math.sin(-ω), 0});
        A1.setRow(1, new double[]{-Math.sin(-ω), Math.cos(-ω), 0});
        A1.setRow(2, new double[]{0, 0, 1});

        RealMatrix A2 = MatrixUtils.createRealMatrix(3, 3);
        A2.setRow(0, new double[]{1, 0, 0});
        A2.setRow(1, new double[]{0, Math.cos(-i), Math.sin(-i)});
        A2.setRow(2, new double[]{0, -Math.sin(-i), Math.cos(-i)});

        RealMatrix A3 = MatrixUtils.createRealMatrix(3, 3);
        A3.setRow(0, new double[]{Math.cos(-Ω), Math.sin(-Ω), 0});
        A3.setRow(1, new double[]{-Math.sin(-Ω), Math.cos(-Ω), 0});
        A3.setRow(2, new double[]{0, 0, 1});

        //计算卫星向径
        double r = a * (1 - e * Math.cos(M));
        double v = Math.sqrt(K * (2 / r - 1 / a));

        //计算卫星在2纬轨道面上的x,y,z坐标和速度
        double x = r * Math.cos(M);
        double y = r * Math.sin(M);
        double z = 0;
        RealVector R = MatrixUtils.createRealVector(new double[]{x, y, z});
        double Vx = v * -Math.sin(M);
        double Vy = v * Math.cos(M);
        double Vz = 0;
        RealVector V = MatrixUtils.createRealVector(new double[]{Vx, Vy, Vz});
        //将2纬轨道坐标进行第一次旋转得到新的坐标矩阵和速度矩阵
        R = A1.operate(R);
        V = A1.operate(V);
        //进行第二次旋转
        R = A2.operate(R);
        V = A2.operate(V);
        //进行第三次旋转，得到ECI坐标下的瞬时时刻坐标和速度
        R = A3.operate(R);
        V = A3.operate(V);
    }
}
