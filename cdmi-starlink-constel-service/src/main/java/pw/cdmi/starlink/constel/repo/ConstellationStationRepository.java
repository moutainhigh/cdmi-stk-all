package pw.cdmi.starlink.constel.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.constel.modules.entities.Station;

@Repository
public interface ConstellationStationRepository extends JpaRepository<Station, String>{
	//获取指定星座下的信关站列表
	public List<Station> findByConstellationId(String constellationId);
	
	//根据信关站的OpenId和星座的OpenId获取卫星信息
	public Station findOneByConstellationIdAndStationId(String constelId, String stationlId);
	
	//获取指定星座下的特定类型信关站列表
	public List<Station> findByConstellationIdAndIsMovable(String constellationId, boolean isMovable);
	
	public void deleteByConstellationIdAndStationId(String constelId, String stationlId);
}
