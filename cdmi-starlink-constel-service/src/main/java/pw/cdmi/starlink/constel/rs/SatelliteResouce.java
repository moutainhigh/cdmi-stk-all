package pw.cdmi.starlink.constel.rs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.cdmi.starlink.constel.modules.SatelliteResponse;
import pw.cdmi.starlink.constel.modules.StationResponse;
import pw.cdmi.starlink.constel.modules.entities.Constellation;
import pw.cdmi.starlink.constel.modules.entities.Group;
import pw.cdmi.starlink.constel.modules.entities.Satellite;
import pw.cdmi.starlink.constel.modules.entities.Station;
import pw.cdmi.starlink.constel.services.SatelliteService;

@RestController
@RequestMapping("/cfg/v1")
public class SatelliteResouce {
	@Autowired
	private SatelliteService service;

	// 新增一个卫星
	@PostMapping("/satel")
	public void postSatellite(@RequestParam("constelId") String constelId, @RequestBody Satellite satel) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		if (satel == null || satel.getSatelliteId() == null) {
			throw new RuntimeException("请求参数中卫星编号是必填项");
		}
		if (StringUtils.isBlank(satel.getSatelliteName())) {
			throw new RuntimeException("请求参数中卫星名称是必填项");
		}

		service.addSatellite(constelId, satel);
	}

	// 删除指定卫星
	@DeleteMapping("/satel/{satelId}")
	public void deleteSatellite(@PathVariable("satelid") String satelId) {
		if (StringUtils.isBlank(satelId)) {
			throw new RuntimeException("请求参数中卫星编号是必填项");
		}
		service.deleteSatellite(satelId);
	}

	// 获取指定星座下的卫星列表
	@GetMapping("/satel")
	public List<SatelliteResponse> listSatelByConstel(@RequestParam("constelid") String constelId) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		// 判断constelid是否存在
		Constellation constel = service.getConstellation(constelId);
		if (constel == null) {
			throw new RuntimeException("没有找到星座编号");
		}
		List<Satellite> ls_satel = service.getSatelsByConstel(constelId, true);
		// 根据卫星的ID，远程获取卫星最新轨迹以及描述信息
		List<SatelliteResponse> list = new ArrayList<SatelliteResponse>();
		for (Satellite sl : ls_satel) {
			SatelliteResponse satel = new SatelliteResponse();
			satel.setName(sl.getSatelliteName());
			satel.setOpenId(sl.getSatelliteId());
			satel.setGroupId(sl.getGroupId());
			Group group = service.getGroup(sl.getGroupId());
			satel.setGroupName(group.getName());
			satel.setConstelId(constel.getOpenId());
			satel.setConstelName(constel.getName());
			list.add(satel);
		}
		return list;
	}

	// 修改卫星归属星座
	@PutMapping("/satel/{satelid}/constel")
	public void putSatelInConstel(@PathVariable("satelid") String satelId,
			@RequestParam("constelid") String constelId) {
		
		if (StringUtils.isBlank(satelId)) {
			throw new RuntimeException("请求参数中卫星编号是必填项");
		}
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		service.changeSatelToConstel(satelId, constelId);;
	}

	// 修改卫星归属分组
	@PutMapping("/satel/{satelid}/group")
	public void putSatelInGroup(@PathVariable("satelid") String satelId, @RequestParam("groupid") String groupId) {
		if (StringUtils.isBlank(satelId)) {
			throw new RuntimeException("请求参数中卫星编号是必填项");
		}
		if (StringUtils.isBlank(groupId)) {
			throw new RuntimeException("请求参数中卫星分组编号是必填项");
		}
		service.changeSatelToGroup(satelId, groupId);;
	}

	// 新增一个星座信息
	@PostMapping("/constel")
	public String postConstellation(Constellation constel) {
		if(constel ==null || StringUtils.isBlank(constel.getName())) {
			throw new RuntimeException("请求参数中星座名称是必填项");
		}
		return service.addConstellation(constel);
	}

	// 编辑指定星座信息
	@PutMapping("/constel/{constelid}")
	public void putConstellation(@PathVariable("constelid") String constelId, Constellation constel) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		if(constel ==null || StringUtils.isBlank(constel.getName())) {
			throw new RuntimeException("请求参数中星座名称是必填项");
		}
		
		service.editConstellation(constelId, constel);
	}

	// 删除指定星座信息
	@DeleteMapping("/constel/{constelid}")
	public void deleteConstellation(@PathVariable("constelid") String constelId) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		
		service.deleteConstellation(constelId);
	}

	// 获取星座列表
	@GetMapping("/constel")
	public List<Constellation> listConstellation() {
		return service.listConstellation();
	}

	// 新增一个星座卫星分组信息
	@PostMapping("/group")
	public String postConstellationGroup(@RequestParam("constelid") String constelId, Group group) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		if (group== null || StringUtils.isBlank(group.getName())) {
			throw new RuntimeException("请求参数中卫星分组名称是必填项");
		}
		
		return service.addConstellationGroup(constelId, group);
	}

	// 编辑指定星座卫星分组信息
	@PutMapping("/group/{groupid}")
	public void putConstellationGroup(@PathVariable("groupid") String groupId, Group group) {
		if (StringUtils.isBlank(groupId)) {
			throw new RuntimeException("请求参数中卫星分组编号是必填项");
		}
		if (group== null) {
			return;  //容错性设计
		}
		service.editConstellationGroup(groupId, group);
	}

	// 获取指定星座下的卫星分組信息列表
	@GetMapping("/constel/{constelid}/group")
	public List<Group> listConstellationGroup(@PathVariable("constelid") String constelid) {
		if (StringUtils.isBlank(constelid)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		return service.getConstellationGroup(constelid);
	}

	// 删除指定星座卫星分组信息
	@DeleteMapping("/group/{groupid}")
	public void deleteConstellationGroup(@PathVariable("groupid") String groupId) {
		if (StringUtils.isBlank(groupId)) {
			throw new RuntimeException("请求参数中卫星分组编号是必填项");
		}
		service.deleteConstellationGroup(groupId);
	}

	// 获取指定星座下指定卫星分組内的卫星列表
	@GetMapping("/group/{groupid}/satel")
	public List<SatelliteResponse> listSatelByGroup(@PathVariable("groupid") String groupId) {
		if (StringUtils.isBlank(groupId)) {
			throw new RuntimeException("请求参数中卫星分组编号是必填项");
		}
		
		// 检查groupId是否存在
		Group group = service.getGroup(groupId);
		if (group == null) {
			throw new RuntimeException("没有找到卫星分组编号");
		}
		
		List<Satellite> ls_satel = service.getSatelsByGroup(groupId);
		List<SatelliteResponse> list = new ArrayList<SatelliteResponse>();
		for (Satellite sl : ls_satel) {
			SatelliteResponse satel = new SatelliteResponse();
			satel.setName(sl.getSatelliteName());
			satel.setOpenId(sl.getSatelliteId());
			satel.setGroupId(sl.getGroupId());
			satel.setGroupName(group.getName());
			Constellation constel = service.getConstellation(group.getConstellationId());
			if (constel == null) {
				throw new RuntimeException("没有找到星座编号,系统数据被破坏");
			}
			satel.setConstelId(constel.getOpenId());
			satel.setConstelName(constel.getName());
			list.add(satel);
		}
		return list;
	}

	// 新增一个星座与信关站的关联信息
	@PutMapping("/station")
	public String putStationInConstel(@RequestParam("constelid") String constelId, Station station) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		if (station == null  || StringUtils.isBlank(station.getStationId())) {
			throw new RuntimeException("请求参数中信关站编号是必填项");
		}
		if(StringUtils.isBlank(station.getName())) {
			throw new RuntimeException("请求参数中信关站名称是必填项");
		}
		return service.addStation(constelId, station);
	}

	// 获取指定星座下的指定类型的信关站列表
	@GetMapping("/station")
	public List<StationResponse> listStationByConstel(@RequestParam("constelid") String constelId,
			@RequestParam("movable") boolean isMovable) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		List<Station> ls_station = service.listStationByConstel(constelId, isMovable);
		List<StationResponse> list = new ArrayList<StationResponse>();
		for (Station st : ls_station) {
			StationResponse station = new StationResponse();
			station.setName(st.getName());
			station.setOpenId(st.getStationId());
			
			Constellation constel = service.getConstellation(constelId);
			if (constel == null) {
				throw new RuntimeException("没有找到星座编号,系统数据被破坏");
			}
			station.setConstelId(constel.getOpenId());
			station.setConstelName(constel.getName());
			list.add(station);
		}
		return list;
	}

	// 解除一个星座与信关站的关联信息
	@DeleteMapping("/station/{stationid}")
	public void deleteStationFormConstel(@RequestParam("constelid") String constelId, @PathVariable("stationid") String stationId) {
		if (StringUtils.isBlank(constelId)) {
			throw new RuntimeException("请求参数中星座编号是必填项");
		}
		if (StringUtils.isBlank(stationId)) {
			throw new RuntimeException("请求参数中信关站编号是必填项");
		}
		service.deleteStation(constelId, stationId);
	}

}
