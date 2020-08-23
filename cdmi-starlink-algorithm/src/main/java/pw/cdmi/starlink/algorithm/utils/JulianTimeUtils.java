package pw.cdmi.starlink.algorithm.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 儒略历时间计算工具类
 */
public class JulianTimeUtils {
    /**
     * 将儒略历时间转换为UTC时间
     */
    public static Date julianTime2Date(int epochYear, double epochDay){
        final int secPerMin = 60;
        final int secPerHour = secPerMin * 60;
        final int secPerDay = secPerHour * 24;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int day = (int)epochDay;
        double tod = (epochDay -day) * secPerDay;

        int hour = (int)(tod/(double) secPerHour);
        int minute = (int)((tod-(double)hour * secPerHour)/secPerMin);
        int second = (int)((tod-(double)(hour * secPerHour + minute * secPerMin)));
        int milsec = (int)((tod - (int)tod) * 1000.0);
        cal.set(Calendar.YEAR,((epochYear + 50) % 100) + 1950);
        cal.set(Calendar.DAY_OF_YEAR,day);
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND,second);
        cal.set(Calendar.MILLISECOND, milsec);

        return cal.getTime();
    }

    public static double Date2Julian(Calendar calendar){
        double b=0,c=0;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        int y = year, m = month;

        if(m <=2){
            y = y -1;
            m = m + 12;
        }
        if(y<0){
            c = -0.75;
        }
        int a;
        if(year > 1582 || month > 10 || day > 14){
            a = (int)(y / 100);
            b = 2 -a + Math.floor(a/4);
        }

        double jd = (int)(365.25 * y + c) + (int)(30.6001 * (m +1));
        jd = jd + day + b + 1720994.5;
        jd = jd + (hour + min/60 + sec/3600)/24;
        return  jd - 2400000.5;
    }

    public static double Date2Julian(int year, int month, int day){
        double djm0 = 2400000.5;
        double b=0,c=0;

        if (month <= 2) {
            year = year - 1;
            month = month + 12;
        }

        if (year < 0) {
            c = -.75;
        }
        int a;
        if(year > 1582 || month > 10 || day > 14){
            a = (int)(year / 100);
            b = 2 -a + Math.floor(a/4);
        }

        double jd = (int)(365.25 * year + c) + (int)(30.6001 * (month +1));
        jd = jd + day + b + 1720994.5;
        return jd-djm0;
    }

    public static Calendar Julian2Calendar(double jd){
        int z = (int)(jd + 0.5);
        double fday = jd + .5 - z;
        int a,b,c,d,e;
        double sec,min,hr,day,month,year;

        if (fday < 0) {
            fday = fday + 1;
            z = z - 1;
        }

        if (z < 2299161) {
            a = z;
        }else {
            int alpha = (int)Math.floor((z - 1867216.25) / 36524.25);
            a = z + 1 + alpha - (int)Math.floor(alpha / 4);
        }

        b = a + 1524;
        c = (int)((b - 122.1) / 365.25);
        d = (int)(365.25 * c);
        e = (int)((b - d) / 30.6001);
        day = b - d - (int)(30.6001 * e) + fday;

        if (e < 14) {
            month = e - 1;
        }else {
            month = e - 13;
        }

        if (month > 2) {
            year = c - 4716;
        }else {
            year = c - 4715;
        }

        hr = Math.abs(day-Math.floor(day))*24;
        min = Math.abs(hr-Math.floor(hr))*60;
        sec = Math.abs(min-Math.floor(min))*60;

        day = Math.floor(day);
        hr = Math.floor(hr);
        min = Math.floor(min);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set((int)year, (int)month, (int)day, (int)hr, (int)min, (int)sec);
        return calendar;
    }
}
