package pw.cdmi.starlink.constel.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.constel.modules.entities.Satellite;

@Repository
public interface ConstellationSatelliteRepository extends JpaRepository<Satellite, String>{
	
	//根据卫星的OpenId获取卫星信息
	public Satellite findOneBySatelliteId(String satelId);
	
	//获取指定星座下的卫星信息
	public List<Satellite> findByConstellationId(String constellationId);
	
	//获取指定卫星分组下的卫星信息
	public List<Satellite> findByGroupId(String groupId);
	
	//从指定的星座下移除指定卫星
	public void deleteByConstellationIdAndSatelliteId(String constellationId, String satelliteId);

}
