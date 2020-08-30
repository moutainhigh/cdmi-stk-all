package pw.cdmi.starlink.constel.modules;

public class StationResponse {
	private String openId;
	private String name;
	private String constelId;
	private String constelName;
	private String isInNet;
	
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
	public String getConstelId() {
		return constelId;
	}
	public void setConstelId(String constelId) {
		this.constelId = constelId;
	}
	public String getConstelName() {
		return constelName;
	}
	public void setConstelName(String constelName) {
		this.constelName = constelName;
	}
	public String getIsInNet() {
		return isInNet;
	}
	public void setIsInNet(String isInNet) {
		this.isInNet = isInNet;
	}
}
