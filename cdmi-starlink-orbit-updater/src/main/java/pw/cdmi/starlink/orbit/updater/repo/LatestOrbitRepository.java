package pw.cdmi.starlink.orbit.updater.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.orbit.updater.modules.entities.LatestOrbit;

@Repository
public interface LatestOrbitRepository extends JpaRepository<LatestOrbit, String>{

	//从当前卫星轨道表中根据卫星的NoradId获取卫星最新轨道信息
	public LatestOrbit getByNoradId(String noradId);
}
