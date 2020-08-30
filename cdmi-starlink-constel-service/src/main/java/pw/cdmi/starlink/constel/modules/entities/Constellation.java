package pw.cdmi.starlink.constel.modules.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "constellation")
public class Constellation implements Serializable{

	private static final long serialVersionUID = 7115032078572446472L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "OpenId", unique = true, nullable = false, length = 32)
	private String openId;
	@Column(name = "Name")
	private String name;
	@Column(name = "OwnerShip")
	private String ownerShip;
	@Column(name = "isManage")
	private boolean isManage;			//是否可管理
	@Column(name = "Desc")
	private String desc;
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwnerShip() {
		return ownerShip;
	}

	public void setOwnerShip(String ownerShip) {
		this.ownerShip = ownerShip;
	}

	
	public boolean isManage() {
		return isManage;
	}

	public void setManage(boolean isManage) {
		this.isManage = isManage;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
