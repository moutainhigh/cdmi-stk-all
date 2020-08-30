package pw.cdmi.starlink.constel.modules.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 对应星座卫星关联表
 * @author 伍伟
 *
 */
@Entity(name = "constellation_satellite")
public class Satellite implements Serializable{

	private static final long serialVersionUID = 1557573535830402540L;
	
	@Id
	@Column(name = "Satellite_Id",nullable = false, length = 32)
	private String satelliteId;
	@Column(name = "Constellation_Id",nullable = false, length = 32)
	private String constellationId;
	@Column(name = "SatelliteName")
	private String satelliteName;
	@Column(name = "Desc")
	private String desc;
	@Column(name = "Group_Id",nullable = false, length = 32)
	private String groupId;
	@Column(name = "IsInNet")
	private boolean isInNet;
	

	public String getConstellationId() {
		return constellationId;
	}

	public void setConstellationId(String constellationId) {
		this.constellationId = constellationId;
	}

	public String getSatelliteId() {
		return satelliteId;
	}

	public void setSatelliteId(String satelliteId) {
		this.satelliteId = satelliteId;
	}

	public String getSatelliteName() {
		return satelliteName;
	}

	public void setSatelliteName(String satelliteName) {
		this.satelliteName = satelliteName;
	}

	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isInNet() {
		return isInNet;
	}

	public void setInNet(boolean isInNet) {
		this.isInNet = isInNet;
	}
	
}
