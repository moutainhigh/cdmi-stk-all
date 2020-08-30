package pw.cdmi.starlink.constel.modules.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * 对应星座卫星分组表
 * @author 伍伟
 *
 */
@Entity(name = "satellite_groups")
public class Group implements Serializable{

	private static final long serialVersionUID = 612271389708154988L;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "Id", unique = true, nullable = false, length = 32)
	private String id;
	@Column(name = "Constellation_Id",nullable = false, length = 32)
	private String constellationId;
	@Column(name = "Name")
	private String name;
	@Column(name = "Desc")
	private String desc;
	
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
}
