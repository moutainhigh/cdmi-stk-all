package pw.cdmi.starlink.station.timewindow.rc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pw.cdmi.starlink.algorithm.OrbitTraceTime;
import pw.cdmi.starlink.algorithm.utils.TransUtils;
import pw.cdmi.starlink.core.datetime.TimeSlice;
import pw.cdmi.starlink.core.orbit.Ecef;
import pw.cdmi.starlink.core.orbit.Globals;
import pw.cdmi.starlink.core.station.ElevationSection;
import pw.cdmi.starlink.station.FixedStation;
import pw.cdmi.starlink.station.timewindow.OrbitTimeWindow;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/timewindow/v1/")
public class TimeWindowCalculateResource {

	public List<OrbitTimeWindow> listTimeWindowWithStation(List<OrbitTraceTime> tracePoints, FixedStation station) {
		// 获得地面站的ECEF坐标
		double longitude = station.getGeo().getLongitudeRad();
		double latitude = station.getGeo().getLatitudeRad();
		Ecef station_ecef = TransUtils.WGS842ECEF(longitude, latitude, station.getGeo().getAltitude());
		// 将地面站的ECEF坐标旋转到地面站与地心线后的坐标位置
		Ecef rataion_station_ecef = TransUtils.ecef2env(station_ecef.getPosition().X, station_ecef.getPosition().Y,
				station_ecef.getPosition().Z, longitude, latitude);

		double x2 = rataion_station_ecef.getPosition().X;
		double y2 = rataion_station_ecef.getPosition().Y;
		double z2 = rataion_station_ecef.getPosition().Z;

		// 计算信关站仰角
		List<OrbitTimeWindow> timewindows = new ArrayList<OrbitTimeWindow>();
		OrbitTimeWindow timewindow = new OrbitTimeWindow();
		TimeSlice slice = new TimeSlice();
		ElevationSection section = new ElevationSection();

		double tw_ele = station.getMinElevation() * Globals.RadsPerDegree;

		double last_elevation = -1.0;
		OrbitTraceTime last_trace = null;
		for (OrbitTraceTime trace : tracePoints) {
			double x = trace.getEcef().getPosition().X;
			double y = trace.getEcef().getPosition().Y;
			double z = trace.getEcef().getPosition().Z;
			// 将卫星的ECEF坐标旋转到地面站与地心线的位置
			Ecef rataion_satel_ecef = TransUtils.ecef2env(x, y, z, longitude, latitude);

			double x1 = rataion_satel_ecef.getPosition().X;
			double y1 = rataion_satel_ecef.getPosition().Y;
			double z1 = rataion_satel_ecef.getPosition().Z;
			// 得到信关站仰角
			double elevation = TransUtils.getElevation(x1, y1, z1, x2, y2, z2);
			
			if(elevation > tw_ele) {
				if(slice.getBeginTime() == null) {
					slice.setBeginTime(trace.getEpochTime());
					section.setStart(elevation * Globals.DegreesPerRad);
				}
			} else {
				if(slice.getBeginTime() != null) {
					if(elevation < tw_ele) {//有部分带毫秒时刻的仰角低于仰角要求，需要回退到上一个轨迹时间
						slice.setEndTime(last_trace.getEpochTime());
						section.setEnd(last_elevation * Globals.DegreesPerRad);
					}else {
						slice.setEndTime(trace.getEpochTime());
						section.setEnd(elevation * Globals.DegreesPerRad);
					}
					timewindow.setTimeSlice(slice);
					timewindow.setFixedStationElevation(section);
					//保证完整时间窗
					timewindows.add(timewindow);
					section = new ElevationSection();
					slice = new TimeSlice();
					timewindow = new OrbitTimeWindow();
				}
			}
			last_elevation = elevation;
			last_trace = trace;
		}
		//最后时间窗未关闭
		if(slice.getBeginTime() != null && slice.getEndTime() == null) {
			slice.setEndTime(last_trace.getEpochTime());
			section.setEnd(last_elevation * Globals.DegreesPerRad);
			timewindow.setTimeSlice(slice);
			timewindow.setFixedStationElevation(section);
			timewindows.add(timewindow);
		}
		return timewindows;
	}
}
