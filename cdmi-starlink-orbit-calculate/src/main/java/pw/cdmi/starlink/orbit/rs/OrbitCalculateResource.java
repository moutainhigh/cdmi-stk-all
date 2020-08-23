package pw.cdmi.starlink.orbit.rs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.cdmi.starlink.algorithm.OrbitTraceTime;
import pw.cdmi.starlink.algorithm.orbit.EciTime;
import pw.cdmi.starlink.algorithm.orbit.sgp4.SatElset;
import pw.cdmi.starlink.core.orbit.Tle;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/trace/v1")
public class OrbitCalculateResource {

	/**
	 * 
	 * @param startime      	要获取的轨道的开始时间
	 * @param endtime        	要获取的轨道的结束时间
	 * @param tle        		轨道两行数据
	 * @param step       		轨道时间间隔，单位为秒
	 * @param secDisplay 		是否整秒显示
	 * @return
	 */
	@GetMapping("/sdp4")
	public List<OrbitTraceTime> calculateOrbitTraceWithSdp4(@RequestParam("startime") Date startime,
			@RequestParam("endtime") Date endtime, Tle tle, @RequestParam("step") int step,
			@RequestParam("secdisplay") boolean secDisplay) {
		SatElset sat = new SatElset(tle);
		Date current = sat.getEpochTime();

		long s_mpe = startime.getTime() - current.getTime();
		long e_mpe = endtime.getTime() - current.getTime();
		List<OrbitTraceTime> points = new ArrayList<OrbitTraceTime>();
		OrbitTraceTime point = null;

		if (secDisplay) {
			// 获取整秒时间点的轨迹
			EciTime eci = sat.PositionEci((double) s_mpe / 1000);
			point = new OrbitTraceTime(eci, new Date(current.getTime() + s_mpe));
			points.add(point);
			s_mpe = s_mpe + (1000 - startime.getTime() % 1000);
			e_mpe = e_mpe - endtime.getTime() % 1000;
		}

		for (long mpe = s_mpe; mpe <= e_mpe; mpe += step * 1000) {
			double time = (double) mpe / 1000;
			EciTime eci = sat.PositionEci(time);
			point = new OrbitTraceTime(eci, new Date(current.getTime() + mpe));
			points.add(point);
		}
		return points;
	}
}
