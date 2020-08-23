package pw.cdmi.starlink.core.orbit;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * // ////////////////////////////////////////////////////////////////////////
 * // // NASA Two-Line Element Data format // // [Reference: Dr. T.S. Kelso /
 * www.celestrak.com] // // Two-line element data consists of three lines in the
 * following format: // // AAAAAAAAAAAAAAAAAAAAAAAA // 1 NNNNNU NNNNNAAA
 * NNNNN.NNNNNNNN +.NNNNNNNN +NNNNN-N +NNNNN-N N NNNNN // 2 NNNNN NNN.NNNN
 * NNN.NNNN NNNNNNN NNN.NNNN NNN.NNNN NN.NNNNNNNNNNNNNN // // Line 0 is a
 * twenty-four-character name. // // Lines 1 and 2 are the standard Two-Line
 * Orbital Element Set Format identical // to that used by NORAD and NASA. The
 * format description is: // // Line 1 // Column Description // 01-01 Line
 * Number of Element Data // 03-07 Satellite Number // 10-11 International
 * Designator (Last two digits of launch year) // 12-14 International Designator
 * (Launch number of the year) // 15-17 International Designator (Piece of
 * launch) // 19-20 Epoch Year (Last two digits of year) // 21-32 Epoch (Julian
 * Day and fractional portion of the day) // 34-43 First Time Derivative of the
 * Mean Motion // or Ballistic Coefficient (Depending on ephemeris type) //
 * 45-52 Second Time Derivative of Mean Motion (decimal point assumed; // blank
 * if N/A) // 54-61 BSTAR drag term if GP4 general perturbation theory was used.
 * // Otherwise, radiation pressure coefficient. (Decimal point assumed) //
 * 63-63 Ephemeris type // 65-68 Element number // 69-69 Check Sum (Modulo 10)
 * // (Letters, blanks, periods, plus signs = 0; minus signs = 1) // Line 2 //
 * Column Description // 01-01 Line Number of Element Data // 03-07 Satellite
 * Number // 09-16 Inclination [Degrees] // 18-25 Right Ascension of the
 * Ascending Node [Degrees] // 27-33 Eccentricity (decimal point assumed) //
 * 35-42 Argument of Perigee [Degrees] // 44-51 Mean Anomaly [Degrees] // 53-63
 * Mean Motion [Revs per day] // 64-68 Revolution number at epoch [Revs] //
 * 69-69 Check Sum (Modulo 10) // // All other columns are blank or fixed. // //
 * Example: // // NOAA 6 // 1 11416U 86 50.28438588 0.00000140 67960-4 0 5293 //
 * 2 11416 98.5105 69.3305 0012788 63.2828 296.9658 14.24899292346978
 */
public class Tle {
	public enum Line {
		Zero, One, Two
	}

	public enum Field {
		L1_NoradNum(2, 5), // 卫星编号
		L1_IntlDesc(9, 8), // 卫星发射年份后两位 + 三位当年发射顺序 + 三位卫星发射数量
		L1_EpochYear(18, 2), // Epoch: Last two digits of year/儒略历中的两位年
		L1_EpochDay(20, 12), // Epoch: Fractional Julian Day of year儒略历中的日数（含整数与小数)
		L1_MeanMotionDt(33, 10), // First time derivative of mean motion
		L1_MeanMotionDt2(44, 8), // Second time derivative of mean motion
		L1_BStarDrag(53, 8), // BSTAR Drag ;
		L1_SetNumber(64, 4), // TLE set number/星历编号，TLE数据按新发现卫星的先后顺序的编号
		L2_Inclination(8, 8), // Inclination/轨道的交角是指天体的轨道面和地球赤道面之间的夹度
		L2_Raan(17, 8), // R.A. ascending node/升交点赤经，升交点赤经是指卫星由南到北穿过地球赤道平面时，与地球赤道平面的交点
		L2_Eccentricity(26, 7), // Eccentricity/轨道偏心率
		L2_ArgPerigee(34, 8), // Argument of perigee/近地点幅角
		L2_MeanAnomaly(43, 8), // Mean anomaly / 平近点角
		L2_MeanMotion(52, 11), // Mean motion / 每天环绕地球的圈数
		L2_OrbitNumAtEpoch(63, 5); // Orbit cycles number at epoch / 发射以来飞行的圈数

		private int beginIndex;
		private int endIndex;

		Field(int col, int len) {
			this.beginIndex = col;
			this.endIndex = col + len;
		}

		public int getBeginIndex() {
			return beginIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}
	}

	// Satellite name and two data lines
	private String m_Line0;
	private String m_Line1;
	private String m_Line2;

	public String getName() {
		return this.m_Line0;
	}

	public String getLine1() {
		return m_Line1;
	}

	public String getLine2() {
		return m_Line2;
	}

	private static final int THREELINES = 3;
//    private static final int TLE_LEN_LINE_NAME = 24;
//    private static final int TLE_LEN_LINE_DATA = 69;
//    private static final double DEG2RAD = 1.745329251994330E-2;
//    private static final double TWO_PI = Math.PI * 2.0;
//    private static final double MINS_PERDAY = 1.44E3;
//    private static final double XKE = 7.43669161E-2;
//    private static final double TWO_THIRDS = 2.0 / 3.0;
//    private static final double CK2 = 5.413079E-4;
//
//    private String catnum;
//    private String setnum;
//    private int year;
//    private double refepoch;
//    private double incl;
//    private double eccn;
//    private double argper;
//    private double meanan;
//    private double meanmo;
//    private double ndot;
//    private double nddot6;
//    private double bstar;
//    private int orbitnum;
//    private double epoch;
//    private double xndt2o;
//    private double xincl;
//    private double xnodeo;
//    private double eo;
//    private double omegao;
//    private double xmo;
//    private double xno;
//    private boolean deepspace;

	public Tle(Tle tle) {
		this.m_Line0 = tle.m_Line0;
		this.m_Line1 = tle.m_Line1;
		this.m_Line2 = tle.m_Line2;
		initialize();
	}

	public Tle(String line0, String line1, String line2) throws IllegalArgumentException {
		this(new String[] { line0, line1, line2 });
	}

	public Tle(String[] tle) throws IllegalArgumentException {
		if (null == tle) {
			throw new IllegalArgumentException("TLE is null");
		}

		if (tle.length == THREELINES) {
			this.m_Line0 = tle[0];
			this.name = this.m_Line0.trim();
			this.m_Line1 = tle[1];
			this.m_Line2 = tle[2];
		} else if (tle.length == 2) {
			this.m_Line1 = tle[0];
			this.m_Line2 = tle[1];
		} else {
			throw new IllegalArgumentException("TLE format is wrong");
		}

		initialize();

//        catnum = StringUtils.strip(tle[1].substring(2, 7));
//        setnum = StringUtils.strip(tle[1].substring(64, 68));
//        year = Integer.parseInt(StringUtils.strip(tle[1].substring(18, 20)));
//        refepoch = Double.parseDouble(tle[1].substring(20, 32));
//        incl = Double.parseDouble(tle[2].substring(8, 16));
//        raan = Double.parseDouble(tle[2].substring(17, 25));
//        eccn = 1.0e-07 * Double.parseDouble(tle[2].substring(26, 33));
//        argper = Double.parseDouble(tle[2].substring(34, 42));
//        meanan = Double.parseDouble(tle[2].substring(43, 51));
//        meanmo = Double.parseDouble(tle[2].substring(52, 63));
//        ndot = Double.parseDouble(tle[1].substring(33, 43));
//
//        double tempnum = 1.0e-5 * Double.parseDouble(tle[1].substring(44, 50));
//        nddot6 = tempnum
//                / Math.pow(10.0, Double.parseDouble(tle[1].substring(51, 52)));
//
//        tempnum = 1.0e-5 * Double.parseDouble(tle[1].substring(53, 59));
//
//        bstar = tempnum
//                / Math.pow(10.0, Double.parseDouble(tle[1].substring(60, 61)));
//
//        String t_orbitnum = StringUtils.strip(tle[2].substring(63, 68));
//        if (StringUtils.isBlank(t_orbitnum)) {
//            orbitnum = 0;
//        } else {
//            orbitnum = Integer.parseInt(t_orbitnum);
//        }
//
//        /* reassign the values to thse which get used in calculations */
//        epoch = (1000.0 * getYear()) + getRefepoch();
//
//        xndt2o = ndot;
//
//        double temp = incl;
//        temp *= DEG2RAD;
//        xincl = temp;
//
//        temp = raan;
//        temp *= DEG2RAD;
//        xnodeo = temp;
//
//        eo = eccn;
//
//        temp = argper;
//        temp *= DEG2RAD;
//        omegao = temp;
//
//        temp = meanan;
//        temp *= DEG2RAD;
//        xmo = temp;
//
//        xno = meanmo;
//
//        /* Preprocess tle set */
//
//        preProcessTLESet();
	}

	private String name;
	private String noradNum;
	private String intlDesc;
	private int epochYear;
	private double epochDay;
	private double meanMotionDt;
	private double meanMotionDt2;
	private double bstarDrag;
	private String setNumber;
	private double inclination;
	private double raan;
	private double eccentricity;
	private double argumentPerigee;
	private double meanAnomaly;
	private double meanMotion;
	private int orbitNumAtEpoch;

	private void initialize() {
		this.noradNum = getSection(this.m_Line1, Field.L1_NoradNum);
		this.intlDesc = getSection(this.m_Line1, Field.L1_IntlDesc);
		this.epochYear = Integer.parseInt(StringUtils.strip(getSection(this.m_Line1, Field.L1_EpochYear)));
		this.epochDay = Double.parseDouble(getSection(this.m_Line1, Field.L1_EpochDay));
		this.meanMotionDt = Double.parseDouble(getSection(this.m_Line1, Field.L1_MeanMotionDt));
		this.meanMotionDt2 = expToDecimal(getSection(this.m_Line1, Field.L1_MeanMotionDt2));
		this.bstarDrag = expToDecimal(getSection(this.m_Line1, Field.L1_BStarDrag));
		this.setNumber = getSection(this.m_Line1, Field.L1_SetNumber);
		this.inclination = Double.parseDouble(getSection(this.m_Line2, Field.L2_Inclination));
		this.raan = Double.parseDouble(getSection(this.m_Line2, Field.L2_Raan));
		this.eccentricity = 1.0e-07 * Double.parseDouble(getSection(this.m_Line2, Field.L2_Eccentricity));
		this.argumentPerigee = Double.parseDouble(getSection(this.m_Line2, Field.L2_ArgPerigee));
		this.meanAnomaly = Double.parseDouble(getSection(this.m_Line2, Field.L2_MeanAnomaly));
		this.meanMotion = Double.parseDouble(getSection(this.m_Line2, Field.L2_MeanMotion));

		String str_orbitNumAtEpoch = getSection(this.m_Line2, Field.L2_OrbitNumAtEpoch).trim();
		if (StringUtils.isBlank(str_orbitNumAtEpoch)) {
			this.orbitNumAtEpoch = 0;
		} else {
			this.orbitNumAtEpoch = Integer.parseInt(str_orbitNumAtEpoch);
		}
		
	}

	/**
	 * 获取两行数据中的指定行列信息片段
	 * 
	 * @param line  两行数据中的行内容
	 * @param field 要获取的行列信息
	 * @return
	 */
	private String getSection(String line, Field field) {
		return line.substring(field.getBeginIndex(), field.getEndIndex());
	}

//    private synchronized void preProcessTLESet() {
//        double temp;
//        temp = TWO_PI / MINS_PERDAY / MINS_PERDAY;
//        xno = xno * temp * MINS_PERDAY;
//        xndt2o *= temp;
//
//        double dd1 = XKE / xno;
//        final double a1 = Math.pow(dd1, TWO_THIRDS);
//        final double r1 = Math.cos(xincl);
//        dd1 = 1.0 - eo * eo;
//        temp = CK2 * 1.5f * (r1 * r1 * 3.0 - 1.0)
//                / Math.pow(dd1, 1.5);
//        final double del1 = temp / (a1 * a1);
//        final double ao = a1
//                * (1.0 - del1
//                * (TWO_THIRDS * .5 + del1
//                * (del1 * 1.654320987654321 + 1.0)));
//        final double delo = temp / (ao * ao);
//        final double xnodp = xno / (delo + 1.0);
//
//        /* Select a deep-space/near-earth ephemeris */
//
//        deepspace = TWO_PI / xnodp / MINS_PERDAY >= 0.15625;
//    }

	public String getNoradNum() {
		return this.noradNum;
	}

//    public String getCatnum() {
//        return catnum;
//    }
//
//    public String getSetnum() {
//        return setnum;
//    }
//
//    public int getYear() {
//        return year;
//    }
//
//    public double getRefepoch() {
//        return refepoch;
//    }
//
    public double getIncl() {
        return this.inclination;
    }

	public double getRaan() {
		return this.raan;
	}

    public double getEccn() {
        return this.eccentricity;
    }

    public double getArgper() {
        return this.argumentPerigee;
    }

    public double getMeanan() {
    	return this.meanAnomaly;
//        return meanan;
    }

    /**
     * 获得每天环绕地球的圈数
     * @return
     */
    public double getMeanmo() {
    	return this.meanMotion;
//        return meanmo;
    }

    /**
     * 获得发射以来飞行的圈数
     * @return
     */
    public double getOrbitNumAtEpoch() {
    	return this.orbitNumAtEpoch;
    }
    
    public double getNdot() {
        return this.meanMotionDt;
    }

    public double getNddot6() {
    	return this.meanMotionDt2;
//        return nddot6;
    }

    /**
     * 获得BSTAR拖调制系数
     * @return
     */
    public double getBstar() {
    	return this.bstarDrag;
    }
//
//    public int getOrbitnum() {
//        return orbitnum;
//    }
//
//    public double getEpoch() {
//        return epoch;
//    }
//
//    public double getXndt2o() {
//        return xndt2o;
//    }
//
//    public double getXincl() {
//        return xincl;
//    }
//
//    public double getXnodeo() {
//        return xnodeo;
//    }
//
//    public double getEo() {
//        return eo;
//    }
//
//    public double getOmegao() {
//        return omegao;
//    }
//
//    public double getXmo() {
//        return xmo;
//    }
//
//    public double getXno() {
//        return xno;
//    }
//
//    /**
//     * 是否为深空卫星
//     * @return
//     */
//    public boolean isDeepspace() {
//        return deepspace;
//    }
//
    // //////////////////////////////////////////////////////////////////////////
    // Converts TLE-style exponential notation of the form [ |+|-]00000[ |+|-]0
    // to decimal notation. Assumes implied decimal point to the left of the first
    // number in the string, i.e., 
    //       " 12345-3" =  0.00012345
    //       "-23429-5" = -0.0000023429   
    //       " 40436+1" =  4.0436
    // Also assumes that lack of a sign character implies a positive value, i.e.,
    //       " 00000 0" =  0.00000
    //       " 31415 1" =  3.1415
    //FIX ME 参考C#代码
    protected static double expToDecimal(String str)
    {
//       final int COL_SIGN     = 0;
//       final int LEN_SIGN     = 1;
//
//       final int COL_MANTISSA = 1;
//       final int LEN_MANTISSA = 5;
//
//       final int COL_EXPONENT = 6;
//       final int LEN_EXPONENT = 2;
//
//       String sign     = str.substring(COL_SIGN,     LEN_SIGN);
//       String mantissa = str.substring(COL_MANTISSA, LEN_MANTISSA);
//       String exponent = str.substring(COL_EXPONENT, LEN_EXPONENT).trim();

       double tempnum = 1.0e-5 * Double.parseDouble(str.substring(0, 6));

       double val = tempnum
             / Math.pow(10.0, Double.parseDouble(str.substring(7, 8)));
       return val;
    }
    
    public Julian EpochJulian() {
        System.out.println();
        if (epochYear < 57) {
        	epochYear += 2000;
        } else if (epochYear < 100) {
        	epochYear += 1900;
        }

        return new Julian(epochYear, epochDay);
    }

	public Date getEpochTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		int day = (int) epochDay;
		double tod = (epochDay - day) * Globals.SecPerDay;

		int hour = (int) (tod / (double) Globals.SecPerHour);
		int minute = (int) ((tod - (double) hour * Globals.SecPerHour) / Globals.SecPerMin);
		int second = (int) ((tod - (double) (hour * Globals.SecPerHour + minute * Globals.SecPerMin)));
		int milsec = (int) ((tod - (int) tod) * 1000.0);
		cal.set(Calendar.YEAR, ((epochYear + 50) % 100) + 1950);
		cal.set(Calendar.DAY_OF_YEAR, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, milsec);

		return cal.getTime();
	}
}