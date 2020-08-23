package pw.cdmi.starlink.algorithm;

import java.util.Date;

import pw.cdmi.starlink.algorithm.orbit.EciTime;
import pw.cdmi.starlink.algorithm.utils.TransUtils;
import pw.cdmi.starlink.core.orbit.Ecef;
import pw.cdmi.starlink.core.orbit.Eci;
import pw.cdmi.starlink.core.orbit.Geo;

public class OrbitTraceTime {

	private Date epochTime;
	private Eci eci;
	private Geo geo;
	private Ecef ecef;

	public OrbitTraceTime(EciTime eci, Date date) {
		this.epochTime = date;
		this.eci = eci;
		this.geo = new Geo(eci, eci.Date);
		this.ecef = TransUtils.WGS842ECEF(geo.getLongitudeRad(), geo.getLatitudeRad(), geo.getAltitude());
	}

	public Date getEpochTime() {
		return epochTime;
	}

	public Eci getEci() {
		return eci;
	}

	public Geo getGeo() {
		return geo;
	}

	public Ecef getEcef() {
		return ecef;
	}

}
