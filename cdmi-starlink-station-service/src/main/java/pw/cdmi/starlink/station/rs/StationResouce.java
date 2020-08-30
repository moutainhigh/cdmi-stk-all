package pw.cdmi.starlink.station.rs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.cdmi.starlink.station.modules.entities.Station;
import pw.cdmi.starlink.station.services.StationService;

@RestController
@RequestMapping("/station/v1")
public class StationResouce {
	@Autowired
	private StationService service;

	// 新增一个星座与信关站的关联信息
	@PostMapping("/station")
	public String putStationInConstel(Station station) {
		if(station == null  || StringUtils.isBlank(station.getName())) {
			throw new RuntimeException("请求参数中信关站名称是必填项");
		}
		return service.addStation(station);
	}

	// 获取指定星座下的指定类型的信关站列表
	@GetMapping("/station")
	public List<Station> listStation(@RequestParam("movable") Boolean isMovable,@RequestParam("enable") Boolean enable) {
		List<Station> ls_station = service.listStation(isMovable,enable);
		return ls_station;
	}

	// 解除一个星座与信关站的关联信息
	@DeleteMapping("/{stationid}")
	public void deleteStationFormConstel(@PathVariable("stationid") String stationId) {
		if (StringUtils.isBlank(stationId)) {
			throw new RuntimeException("请求参数中信关站编号是必填项");
		}
		service.deleteStation(stationId);
	}

}
