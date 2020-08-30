package pw.cdmi.starlink.orbit.updater.rs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.cdmi.starlink.orbit.updater.modules.SatelliteOption;
import pw.cdmi.starlink.orbit.updater.modules.entities.LatestOrbit;
import pw.cdmi.starlink.orbit.updater.services.OrbitUpdaterService;

@RestController
@RequestMapping("/orbit/updater/v1")
public class OrbitResouce {
	@Autowired
	private OrbitUpdaterService service;

	// 获取指定卫星的轨道属性
	@GetMapping("/properties")
	public Map<String, String> listLatestOrbitProperty(@RequestParam("satelId") String satelId) {
		return null;
	}

	// 获取指定卫星前一次轨道属性
	@GetMapping("/properties/before")
	public Map<String, String> listPreOrbitProperty(@RequestParam("satelId") String satelId) {
		return null;
	}

	// 获取已采集卫星列表
	@GetMapping("/satellite")
	public List<SatelliteOption> listSatellite() {
		List<LatestOrbit>  list= service.listLatestOrbit();
		List<SatelliteOption> op_list = new ArrayList<SatelliteOption>();
		SatelliteOption op = null;
		for(LatestOrbit orbit : list) {
			op = new SatelliteOption(orbit.getOpenId(), orbit.getName());
			op_list.add(op);
		}
		return op_list;
	}

	// 获取指定卫星的最新两行数据，数组长度为3
	@GetMapping("/towline")
	public String[] getLatestOrbitTowLine(@RequestParam("satelId") String satelId) {
		LatestOrbit orbit = service.getLatestOrbit(satelId);
		if (orbit != null) {
			String[] tl = new String[3];
			tl[0] = orbit.getTleName();
			tl[1] = orbit.getLine1();
			tl[2] = orbit.getLine2();
			return tl;
		} else {
			throw new RuntimeException("没有找到编号为[" + satelId + "]的卫星轨道信息");
		}
	}
}
