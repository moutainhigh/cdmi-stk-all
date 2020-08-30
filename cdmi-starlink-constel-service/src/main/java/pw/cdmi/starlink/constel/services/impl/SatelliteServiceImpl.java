package pw.cdmi.starlink.constel.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.starlink.constel.modules.SatelliteResponse;
import pw.cdmi.starlink.constel.modules.entities.Constellation;
import pw.cdmi.starlink.constel.modules.entities.Group;
import pw.cdmi.starlink.constel.modules.entities.Satellite;
import pw.cdmi.starlink.constel.modules.entities.Station;
import pw.cdmi.starlink.constel.repo.ConstellationRepository;
import pw.cdmi.starlink.constel.repo.ConstellationSatelliteRepository;
import pw.cdmi.starlink.constel.repo.ConstellationStationRepository;
import pw.cdmi.starlink.constel.repo.SatelliteGroupRepository;
import pw.cdmi.starlink.constel.services.SatelliteService;

@Service
public class SatelliteServiceImpl implements SatelliteService {
	@Autowired
	private ConstellationRepository c_repo;
	@Autowired
	private ConstellationSatelliteRepository s_repo;
	@Autowired
	private ConstellationStationRepository t_repo;
	@Autowired
	private SatelliteGroupRepository g_repo;

	@Override
	public String addConstellation(Constellation constel) {
		constel.setManage(false);
		c_repo.save(constel);
		return constel.getOpenId();
	}

	@Override
	public void editConstellation(String constelId, Constellation constel) {
		
		Constellation old = c_repo.getOne(constelId);
		boolean isChanged = false;
		if (!StringUtils.isBlank(constel.getName())) {
			old.setName(constel.getName());
			isChanged = true;
		}
		if (!StringUtils.isBlank(constel.getDesc())) {
			old.setName(constel.getDesc());
			isChanged = true;
		}
		if (!StringUtils.isBlank(constel.getOwnerShip())) {
			old.setOwnerShip(constel.getOwnerShip());
			isChanged = true;
		}
		if(isChanged) {
			c_repo.save(old);
		}
	}

	@Override
	public void deleteConstellation(String constelId) {
		//检查星座是否属于运控管理星座，如果是，不允许删除
		Constellation constel = c_repo.getOne(constelId);
		if(constel.isManage()) {
			throw new RuntimeException(String.format("编号为{0}的星座尚在执行运控，不允许删除操作",constelId));
		}
		
		//检查星座下是否有卫星，如果有，不允许删除
		List<Satellite> list = s_repo.findByConstellationId(constelId);
		if(list.size() != 0 ) {
			throw new RuntimeException(String.format("编号为{0}的星座下尚存在{1}颗卫星，不允许删除操作",constelId,list.size()));
		}
		c_repo.deleteById(constelId);
	}

	@Override
	public List<Constellation> listConstellation() {
		return c_repo.findAll();
	}

	@Override
	public Constellation getConstellation(String constelId) {
		return c_repo.getOne(constelId);
	}

	@Override
	public void addSatellite(String constelId, Satellite satel) {
		// 检查星座是否存在
		Constellation constel = c_repo.getOne(constelId);
		if (constel == null) {
			throw new RuntimeException("没有找到编号为" + constelId + "的星座");
		}
		if (!StringUtils.isBlank(satel.getGroupId())) {
			// 判断Group是否存在
			Group group = g_repo.getOne(satel.getGroupId());
			if (group == null) { // 容错性设计
				satel.setGroupId(null);
			}
		}
		// 检查卫星是否存在
		Satellite satel_exist = s_repo.findOneBySatelliteId(satel.getSatelliteId());
		if (satel_exist != null) {
			// 检查卫星当前所属星座是否已加入星座
			if (!satel.getConstellationId().equalsIgnoreCase(constelId)) {
				throw new RuntimeException("该卫星已归属其他星座，请从其他星座中移除后再添加");
			} else {
				if(!StringUtils.isBlank(satel.getSatelliteName())) {
					satel_exist.setSatelliteName(satel.getSatelliteName());
				}
				if(!StringUtils.isBlank(satel.getGroupId())) {
					satel_exist.setGroupId(satel.getGroupId());
				}
				if(!StringUtils.isBlank(satel.getDesc())) {
					satel_exist.setDesc(satel.getDesc());
				}
				s_repo.save(satel_exist);
			}
		} else {
			satel.setConstellationId(constelId);
			satel.setInNet(false); // 默认未入网
			s_repo.save(satel);
		}
	}

	@Override
	public void changeSatelToConstel(String satelId, String constelId) {
		// 检查星座是否存在
		Constellation constel = c_repo.getOne(constelId);
		if (constel == null) {
			throw new RuntimeException(String.format("没有找到对应编号为{0}的星座", constelId));
		}
		// 检查卫星是否存在
		Satellite satel_exist = s_repo.findOneBySatelliteId(satelId);

		if (satel_exist != null) {
			// 检查卫星当前所属星座是否已加入星座
			if (!satel_exist.getConstellationId().equalsIgnoreCase(constelId)) {
				satel_exist.setConstellationId(constelId);
				s_repo.save(satel_exist);
			} // 容错性设计，不返回异常
		} else {
			throw new RuntimeException(String.format("没有找到对应编号为{0}的卫星", satelId));
		}
	}

	public void changeSatelToGroup(String satelId, String groupId) {
		// 检查星座是否存在
		Group group = g_repo.getOne(groupId);
		if (group == null) {
			throw new RuntimeException(String.format("没有找到对应编号为{0}的卫星分组", groupId));
		}
		// 检查卫星是否存在
		Satellite satel_exist = s_repo.findOneBySatelliteId(satelId);

		if (satel_exist != null) {
			// 检查卫星当前所属星座是否已加入星座
			if (!satel_exist.getGroupId().equalsIgnoreCase(groupId)) {
				satel_exist.setGroupId(groupId);
				s_repo.save(satel_exist);
			} // 容错性设计，不返回异常
		} else {
			throw new RuntimeException(String.format("没有找到对应编号为{0}的卫星", satelId));
		}
	}

	@Override
	public List<Satellite> getSatelsByConstel(String constelid, boolean checked) {
		if (!checked) {
			// 判断constelid是否存在
			Constellation constel = c_repo.getOne(constelid);
			if (constel == null) {
				throw new RuntimeException("没有找到星座编号");
			}
		}
		return s_repo.findByConstellationId(constelid);
	}

	@Override
	public void deleteSatellite(String satelId) {
		Satellite satel = s_repo.getOne(satelId);
		if (satel.isInNet()) {
			throw new RuntimeException("该卫星目前在网，在网卫星不能被删除");
		} else {
			// FIXME 不在网卫星也不能被胡乱删除，但现在不做限制
		}
		s_repo.deleteById(satelId);
	}

	@Override
	public String addConstellationGroup(String constelId, Group group) {
		// 判断constelid是否存在
		Constellation constel = c_repo.getOne(constelId);
		if (constel == null) {
			throw new RuntimeException(String.format("没有找到对应编号为{0}的星座", constelId));
		}
		group.setConstellationId(constelId);
		g_repo.save(group);
		return group.getId();
	}

	@Override
	public void editConstellationGroup(String groupId, Group group) {
		Group old = g_repo.getOne(groupId);
		boolean isChanged = false;
		if (!StringUtils.isBlank(group.getName())) {
			old.setName(group.getName());
			isChanged = true;
		}
		if (!StringUtils.isBlank(group.getDesc())) {
			old.setName(group.getDesc());
			isChanged = true;
		}
		if(isChanged) {
			g_repo.save(old);
		}
	}

	@Override
	public List<Group> getConstellationGroup(String constelid) {
		return g_repo.findByConstellationId(constelid);
	}

	public Group getGroup(String groupId) {
		return g_repo.getOne(groupId);
	}

	@Override
	public void deleteConstellationGroup(String groupId) {
		//检查星座下是否有卫星，如果有，不允许删除
		List<Satellite> list = s_repo.findByGroupId(groupId);
		if(list.size() != 0 ) {
			throw new RuntimeException(String.format("编号为{0}的卫星分组下尚存在{1}颗卫星，不允许删除操作",groupId,list.size()));
		}
		g_repo.deleteById(groupId);
	}

	@Override
	public List<Satellite> getSatelsByGroup(String groupId) {
		return s_repo.findByGroupId(groupId);

	}

	@Override
	public String addStation(String constelId, Station station) {
		// 检查constelId是否存在
		Constellation constel = c_repo.getOne(constelId);
		if (constel == null) {
			throw new RuntimeException("没有找到星座编号");
		}
		Station station_exist = t_repo.findOneByConstellationIdAndStationId(constelId, station.getStationId());
		if(station_exist != null) {
			// 检查新关站当前所属星座是否已加入星座
			if (!station_exist.getConstellationId().equalsIgnoreCase(constelId)) {
				throw new RuntimeException("该卫星已归属其他星座，请从其他星座中移除后再添加");
			} else { //容错设计
				if(!StringUtils.isBlank(station.getName())) {
					station_exist.setName(station.getName());
				}
				if(!StringUtils.isBlank(station.getDesc())) {
					station_exist.setDesc(station.getDesc());
				}
				t_repo.save(station);
				return station_exist.getId();
			}
		}else {
			station.setConstellationId(constelId);
			station.setInNet(false); // 默认未入网
			if(station.isMovable() == null) {
				station.setMovable(false);//默认为固定站
			}
			t_repo.save(station);
			return station.getId();		
		}
		
	}

	@Override
	public void changeStationToConstel(String stationId, String constelId) {

	}

	@Override
	public List<Station> getStationsByConstel(String constelid) {
		// 检查constelId是否存在
		Constellation constel = c_repo.getOne(constelid);
		if (constel == null) {
			throw new RuntimeException("没有找到星座编号");
		}
		return t_repo.findByConstellationId(constelid);
	}

	@Override
	public List<Station> listStationByConstel(String constelid, boolean isMovable) {
		// 检查constelId是否存在
		Constellation constel = c_repo.getOne(constelid);
		if (constel == null) {
			throw new RuntimeException("没有找到星座编号");
		}
		return t_repo.findByConstellationIdAndIsMovable(constelid, isMovable);
	}

	@Override
	public void deleteStation(String constelid, String stationId) {
		t_repo.deleteByConstellationIdAndStationId(constelid, stationId);
	}
}
