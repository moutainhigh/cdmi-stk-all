package pw.cdmi.starlink.core;

/**
 * 用以封装空间坐标系中的x，y，z数据
 * @Author 伍伟
 */
public class Coordinate {
    private double x;
    private double y;
    private double z;

    public Coordinate(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
