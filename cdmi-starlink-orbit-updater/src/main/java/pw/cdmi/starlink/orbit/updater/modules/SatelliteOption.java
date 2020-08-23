package pw.cdmi.starlink.orbit.updater.modules;

public class SatelliteOption {
	private String name;
	private String openId;
	
	public SatelliteOption(String name, String openId) {
		this.name = name;
		this.openId = openId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	
	
}
