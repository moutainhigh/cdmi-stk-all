package pw.cdmi.starlink.core.datetime;

import java.io.Serializable;
import java.util.Date;

import pw.cdmi.starlink.core.orbit.Globals;

/**
 * 表示一个时间间隔
 * @Author 伍伟
 */
public class TimeSpan implements Serializable, Comparable<TimeSpan> {
    //表示 1 秒的刻度数。此字段为常数。
    public final static long TicksPerSecond = 1000;
    //表示 1 分钟的刻度数。 此字段为常数。
    public final static long TicksPerMinute = 60000;
    //表示 1 小时的刻度数。 此字段为常数。
    public final static long TicksPerHour = 3600000L;
    //表示一天中的刻度数。 此字段为常数。
    public final static long TicksPerDay = 86400000L;
    // 刻度数，等于两个时间的差值，单位为1毫秒
    private long ticks;

    /**
     * 通过两个时间之差初始化时间间隔对象
     * @param d1 用于时间差计算的第一个时间
     * @param d2 用于时间差计算的第二个时间
     */
    public TimeSpan(Date d1, Date d2){
        this(d1.getTime() - d2.getTime());
    }

    /**
     * 通过两个时间差的毫秒数初始化对象
     * @param millisecond
     */
    public TimeSpan(long millisecond){
        this.ticks = millisecond;
    }

    /**
     * 通过指定的小时数、分钟数和秒数初始化对象
     * @param hours 小时数
     * @param minutes 分钟数
     * @param seconds 秒数
     */
    public TimeSpan(int hours, int minutes, int seconds){
        this.ticks = hours * TicksPerHour + minutes * TicksPerMinute + seconds * TicksPerSecond;
    }

    /**
     * 通过指定的天数、小时数、分钟数和秒数初始化对象
     * @param days 天数
     * @param hours 小时数
     * @param minutes 分钟数
     * @param seconds 秒数
     */
    public TimeSpan(int days, int hours, int minutes, int seconds){
        this.ticks = days * TicksPerDay + hours * TicksPerHour + minutes * TicksPerMinute + seconds * TicksPerSecond;
    }

    /**
     * 通过指定的天数、小时数、分钟数、秒数和毫秒数初始化对象
     * @param days 天数
     * @param hours 小时数
     * @param minutes 分钟数
     * @param seconds 秒数
     * @param milliseconds 毫秒数
     */
    public TimeSpan(int days, int hours, int minutes, int seconds, int milliseconds){
        this.ticks = days * TicksPerDay + hours * TicksPerHour + minutes * TicksPerMinute + seconds * TicksPerSecond + milliseconds;
    }

    /**
     * 获取该实例表示的时间间隔的毫秒数部分
     * @return 此实例的毫秒数部分。 返回值的范围为 -999 到 999。
     */
    public int getMilliseconds() {
        return (int)(this.ticks - (getDays() * Globals.SecPerDay + getHours() * Globals.SecPerHour + getMinutes() * 60 + getSeconds()) * 1000);
    }

    /**
     * 获取该实例表示的时间间隔的秒数部分
     * @return 此实例的秒数部分。 返回值的范围为 -59 到 59。
     */
    public int getSeconds() {
        return (int)(this.ticks / TicksPerSecond - (getDays() * Globals.MinPerDay + getHours() * 60 + getMinutes()) * 60);
    }

    /**
     * 获取该实例表示的时间间隔的分钟数部分
     * @return 此实例的分钟数部分。 返回值的范围为 -59 到 59。
     */
    public int getMinutes() {
        return (int)(this.ticks / TicksPerMinute - (getDays() * Globals.HoursPerDay + getHours()) * 60);
    }

    /**
     * 获取该实例表示的时间间隔的小时数部分
     * @return 此实例的小时数部分。 返回值的范围为 -23 到 23。
     */
    public int getHours() {
        return (int)(this.ticks  / TicksPerHour - getDays() * Globals.HoursPerDay);
    }

    /**
     * 获取该实例表示的时间间隔的天数部分
     * @return 此实例的天数部分。 返回值可以是正数也可以是负数。
     */
    public int getDays() {
        return (int)(this.ticks / TicksPerDay);
    }

    /**
     * 获取该时间间隔包含的刻度数
     * @return 该时间间隔包含的刻度数，单位毫秒
     */
    public long getTicks() { return this.ticks; }

    /**
     * 以整天数和天的小数部分表示的间隔时间
     * @return 表示间隔时间换算为天时的总天数。
     */
    public double getTotalDays() {
        return this.ticks / TicksPerDay;
    }

    /**
     * 以整小时数和小时的小数部分表示的间隔时间
     * @return 表示间隔时间换算为小时的总小时数。
     */
    public double getTotalHours() {
        return this.ticks / TicksPerHour;
    }

    /**
     * 以整分钟数和分钟的小数部分表示的间隔时间
     * @return 表示间隔时间换算为分钟时的总分钟数。
     */
    public double getTotalMinutes() {
        return this.ticks / TicksPerMinute;
    }

    /**
     * 以整秒数和秒的小数部分表示的间隔时间
     * @return 表示间隔时间换算为秒时的总秒数。
     */
    public double getTotalSeconds() {
        return this.ticks / TimeSpan.TicksPerSecond;
    }

    /**
     * 以整毫秒数和毫秒的小数部分表示的间隔时间
     * @return 表示间隔时间换算为毫秒的总毫秒数。
     */
    public double getTotalMilliseconds() {
        return this.ticks;
    }

    /**
     * 将此实例与指定对象进行比较，并返回一个整数，该整数指示此实例是短于、等于还是长于指定对象。
     * @param t 要比较的对象
     * @return -1 表示此实例短于t。 0 表示此实例等于t。 1 表示此实例长于t。
     */
    @Override
    public int compareTo(TimeSpan t) {
        if(this.ticks < t.ticks){
            return 1;
        }else if(this.ticks == t.ticks){
            return 0;
        }
        return -1;
    }

    @Override
    public String toString(){
        return getDays() + "天" + getHours() + "小时" + getMinutes() + "分钟" + getSeconds() + "秒" + getMilliseconds() + "毫秒";
    }
}
