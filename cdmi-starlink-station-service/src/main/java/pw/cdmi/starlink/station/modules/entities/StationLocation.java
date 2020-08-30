package pw.cdmi.starlink.station.modules.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * 信关站位置信息表
 * @author 伍伟
 *
 */
@Entity(name = "station_location")
public class StationLocation implements Serializable{

	private static final long serialVersionUID = -938822364680712829L;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "Id", unique = true, nullable = false, length = 32)
	private String id;
	
	@Column(name = "MoveTime")
	private Date moveTime;
	
	@Column(name = "Station_Id", nullable = false, length = 32)
	private String stationId;
	
	@Column(name = "Longitude")
	private double longitude;
	@Column(name = "Latitude")
	private double latitude;
	@Column(name = "Altitude")
	private double altitude;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getMoveTime() {
		return moveTime;
	}
	public void setMoveTime(Date moveTime) {
		this.moveTime = moveTime;
	}
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	
}
