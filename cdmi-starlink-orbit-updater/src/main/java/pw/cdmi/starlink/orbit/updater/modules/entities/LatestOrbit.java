package pw.cdmi.starlink.orbit.updater.modules.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "orbit_latest")
public class LatestOrbit implements Serializable {

	private static final long serialVersionUID = -3222445454562796103L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "OpenId", unique = true, nullable = false, length = 32)
	private String openId;

	@Column(name = "NoradId", unique = true, nullable = false, length = 5)
	private String noradId;
	@Column(name = "Name")
	private String name;
	@Column(name = "TleName")
	private String tleName;
	@Column(name = "Line1", nullable = false, length = 69)
	private String line1;
	@Column(name = "Line2", nullable = false, length = 69)
	private String line2;
	@Column(name = "TleEpochTime" , nullable = false)
	private Date tleEpochTime;
	@Column(name = "TleLastEpochTime")
	private Date tleLastEpochTime;
	@Column(name = "Propagators")
	private String propagators;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNoradId() {
		return noradId;
	}

	public void setNoradId(String noradId) {
		this.noradId = noradId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTleName() {
		return tleName;
	}

	public void setTleName(String tleName) {
		this.tleName = tleName;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public Date getTleEpochTime() {
		return tleEpochTime;
	}

	public void setTleEpochTime(Date tleEpochTime) {
		this.tleEpochTime = tleEpochTime;
	}

	public Date getTleLastEpochTime() {
		return tleLastEpochTime;
	}

	public void setTleLastEpochTime(Date tleLastEpochTime) {
		this.tleLastEpochTime = tleLastEpochTime;
	}

	public String getPropagators() {
		return propagators;
	}

	public void setPropagators(String propagators) {
		this.propagators = propagators;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String[] listPropagator() {
		if (StringUtils.isBlank(this.propagators)) {
			return null;
		}
		return propagators.split(",");
	}

	public void addPropagator(String propagator) {
		if (StringUtils.isBlank(this.propagators)) {
			this.propagators = propagator;
		} else {
			String[] ps = propagators.split(",");
			boolean exists = false;
			for (String p : ps) {
				if (p.equals(propagator)) {
					exists = true;
				}
			}
			if (!exists) {
				StringBuffer buf = new StringBuffer(this.propagators);
				buf.append(",");
				buf.append(propagator);
				this.propagators = buf.toString();
			}
		}
	}
}
