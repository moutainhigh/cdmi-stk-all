package pw.cdmi.starlink.orbit.updater.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.orbit.updater.modules.entities.HistoryOrbit;

@Repository
public interface HistoryOrbitRepository extends JpaRepository<HistoryOrbit, String>{

	//通过卫星的OpenId以及历史轨道时间历元时间获取历史轨道数据
	public HistoryOrbit findByOpenIdAndTleEpochTime(String openId, Date tleEpochTime);
}
