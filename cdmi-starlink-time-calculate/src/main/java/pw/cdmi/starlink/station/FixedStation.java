package pw.cdmi.starlink.station;

import pw.cdmi.starlink.core.orbit.Geo;

/**
 * 固定地面站
 * @author No.1
 *
 */
public class FixedStation {

	private Geo geo;
	private double minElevation;


	public FixedStation(Geo geo){
		this.geo = geo;
	}
	public Geo getGeo() {
		return geo;
	}
	public double getMinElevation() {
		return this.minElevation;
	}
}
