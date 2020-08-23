package pw.cdmi.starlink.station.timewindow;

import pw.cdmi.starlink.core.datetime.TimeSlice;
import pw.cdmi.starlink.core.station.ElevationSection;

public class OrbitTimeWindow {
	private TimeSlice slice;
	private ElevationSection section;
	
	public TimeSlice getTimeSlice() {
		return this.slice;
	}
	
	public void setTimeSlice(TimeSlice slice) {
		this.slice = slice;
	}
	
	public void setFixedStationElevation(ElevationSection section) {
		this.section = section;
	}
}
