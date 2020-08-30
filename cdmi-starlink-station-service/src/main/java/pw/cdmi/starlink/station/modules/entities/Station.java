package pw.cdmi.starlink.station.modules.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * 信关站信息表
 * @author 伍伟
 *
 */
@Entity(name = "station")
public class Station implements Serializable{
	
	private static final long serialVersionUID = 806204684548934935L;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "OpenId", unique = true, nullable = false, length = 32)
	private String openId;
	@Column(name = "Name")
	private String name;
	@Column(name = "Desc")
	private String desc;
	@Column(name = "Longitude")
	private double longitude;
	@Column(name = "Latitude")
	private double latitude;
	@Column(name = "Altitude")
	private double altitude;
	@Column(name = "IsMovable")
	private Boolean isMovable;
	@Column(name = "LastMoveTime")
	private Date lastMoveTime;
	@Column(name = "Enable")
	private boolean enable;
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String id) {
		this.openId = id;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean isMovable() {
		return isMovable;
	}

	public void setMovable(Boolean isMovable) {
		this.isMovable = isMovable;
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

	public Date getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(Date lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}	
}
