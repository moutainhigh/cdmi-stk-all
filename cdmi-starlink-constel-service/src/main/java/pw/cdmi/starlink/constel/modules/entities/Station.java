package pw.cdmi.starlink.constel.modules.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * 对应星座与信关站关联表
 * @author 伍伟
 *
 */
@Entity(name = "constellation_station")
public class Station implements Serializable{

	private static final long serialVersionUID = 1557573535830402540L;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "Id", unique = true, nullable = false, length = 32)
	private String id;
	@Column(name = "Constellation_Id",nullable = false, length = 32)
	private String constellationId;
	@Column(name = "Station_Id")
	private String stationId;
	@Column(name = "Name")
	private String name;
	@Column(name = "SortNumber")
	private int sortNumber;
	@Column(name = "Desc")
	private String desc;
	@Column(name = "IsMovable")
	private Boolean isMovable;
	@Column(name = "IsInNet")
	private boolean isInNet;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConstellationId() {
		return constellationId;
	}

	public void setConstellationId(String constellationId) {
		this.constellationId = constellationId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(int sortNumber) {
		this.sortNumber = sortNumber;
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

	public boolean isInNet() {
		return isInNet;
	}

	public void setInNet(boolean isInNet) {
		this.isInNet = isInNet;
	}
	
}
