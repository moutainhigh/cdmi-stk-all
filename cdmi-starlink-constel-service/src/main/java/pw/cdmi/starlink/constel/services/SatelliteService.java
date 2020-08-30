package pw.cdmi.starlink.constel.services;

import java.util.List;

import pw.cdmi.starlink.constel.modules.SatelliteResponse;
import pw.cdmi.starlink.constel.modules.entities.Constellation;
import pw.cdmi.starlink.constel.modules.entities.Group;
import pw.cdmi.starlink.constel.modules.entities.Satellite;
import pw.cdmi.starlink.constel.modules.entities.Station;

public interface SatelliteService {
	// 新增一个星座信息
	public String addConstellation(Constellation constel);

	// 编辑指定星座信息
	public void editConstellation(String constelId, Constellation constel);

	// 删除指定星座信息
	public void deleteConstellation(String constelId);

	// 获取星座列表
	public List<Constellation> listConstellation();

	// 得到指定的星座信息
	public Constellation getConstellation(String constelId);
	
	// 导入一个卫星信息
	public void addSatellite(String constelId, Satellite satel);
	
	// 调整指定卫星归属其他星座
	public void changeSatelToConstel(String satelId,String constelId);

	// 调整指定卫星归属其他分组
	public void changeSatelToGroup(String satelId,String groupId);
	
	// 获取指定星座下的卫星列表
	public List<Satellite> getSatelsByConstel(String constelid,boolean checked);

	// 删除指定卫星信息
	public void deleteSatellite(String satelId);
	
	// 新增一个星座卫星分组信息
	public String addConstellationGroup(String constelId, Group group);

	// 编辑指定星座卫星分组信息
	public void editConstellationGroup(String groupId, Group group);

	// 获取指定星座下的卫星分組信息列表
	public List<Group> getConstellationGroup(String constelid);
	
	// 得到指定的星座卫星分组信息
	public Group getGroup(String groupId);
	
	// 删除指定星座卫星分组信息
	public void deleteConstellationGroup(String groupId);

	// 获取指定星座下指定卫星分組内的卫星列表
	public List<Satellite> getSatelsByGroup(String groupId);

	// 新增一个星座与信关站的关联信息
	public String addStation(String constelId, Station station);

	// 调整信关站归属其他星座
	public void changeStationToConstel(String stationId,String constelId);
	
	// 获取指定星座下的信关站列表
	public List<Station> getStationsByConstel(String constelid);

	// 获取指定星座下的指定类型的信关站列表
	public List<Station> listStationByConstel(String constelid, boolean isMovable);
	
	// 删除信关站与星座之间的关系
	public void deleteStation(String constelid, String stationId);
}
