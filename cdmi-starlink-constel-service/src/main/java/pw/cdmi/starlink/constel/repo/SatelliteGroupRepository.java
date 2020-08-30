package pw.cdmi.starlink.constel.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.constel.modules.entities.Group;

@Repository
public interface SatelliteGroupRepository extends JpaRepository<Group, String>{
	//获取指定星座下的卫星分组信息
	public List<Group> findByConstellationId(String constellationId);
	
}
