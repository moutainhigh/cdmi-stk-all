package pw.cdmi.starlink.station.services;

import java.util.List;

import pw.cdmi.starlink.station.modules.entities.Station;


public interface StationService {
	// 新增一个信关站信息
	public String addStation(Station station);

	// 编辑指定的信关站信息
	public void editStation(String stationId,Station station);
	
	// 获取系统内所有的信关站列表
	public List<Station> getAllStations();
	
	// 获取系统内启用或未启用的信关站列表
	public List<Station> getStations(Boolean enable);

	// 获取系统内启用或未启用的指令类型信关站列表
	public List<Station> listStation(Boolean isMovable, Boolean enable);
	
	// 删除指定信关站信息
	public void deleteStation(String stationId);
}
