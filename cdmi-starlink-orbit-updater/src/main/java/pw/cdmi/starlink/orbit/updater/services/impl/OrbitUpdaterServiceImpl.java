package pw.cdmi.starlink.orbit.updater.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.starlink.core.orbit.Tle;
import pw.cdmi.starlink.orbit.updater.modules.entities.HistoryOrbit;
import pw.cdmi.starlink.orbit.updater.modules.entities.LatestOrbit;
import pw.cdmi.starlink.orbit.updater.repo.HistoryOrbitRepository;
import pw.cdmi.starlink.orbit.updater.repo.LatestOrbitRepository;
import pw.cdmi.starlink.orbit.updater.services.OrbitUpdaterService;

@Service
public class OrbitUpdaterServiceImpl implements OrbitUpdaterService{

	@Autowired
	private LatestOrbitRepository repo;
	
	@Autowired
	private HistoryOrbitRepository his_repo;
	
	@Override
	public void readTowLineFileFromRemote(String httpfile) {
		try {
			URL url = new URL(httpfile);
			//字节输入流
			InputStream is = url.openStream();
			//字节流转字符流
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			//再转缓冲流  提高读取效率
			BufferedReader br = new BufferedReader(isr);
			
			String line = null;
			String satelName = null;
			String line1 = null;
			String line2 = null;
			int i = 1;   //每三行数据组成一颗卫星两行数据
			while((line = br.readLine()) != null) {
				if(i % 3 == 1) {
					satelName = line;
				}else if(i % 3 == 2) {
					line1 = line;
				}else {
					line2 = line;
					this.saveNewTowLineTodb(satelName,line1,line2);
				}
				i++;
			}			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Transactional
	@Override
	public void saveNewTowLineTodb(String tlename, String line1, String line2) {
		//解析TLE内容，获取其他信息
		Tle tle = new Tle(tlename,line1,line2);
		String noradId = tle.getNoradNum();
		Date epochTime = tle.getEpochTime();
		
		LatestOrbit orbit = repo.getByNoradId(noradId);
		
		if(orbit != null) {
			if(epochTime.getTime() <= orbit.getTleEpochTime().getTime()) {
				System.out.println("系统中存在比当前两行数据更新的数据，因此该请求不会被处理");
				return;
//				throw new RuntimeException("系统中存在比新添加的两行数据更新的数据，因此该请求不会被处理");
			}else {
				//将原来的LastestOrbit保存到HistoryOrbit表中
				HistoryOrbit his_orbit = his_repo.findByOpenIdAndTleEpochTime(orbit.getOpenId(), orbit.getTleEpochTime());
				if(his_orbit == null) {
					his_orbit = new HistoryOrbit();
					his_orbit.setOpenId(orbit.getOpenId());
				}
				his_orbit.setLine1(orbit.getLine1());
				his_orbit.setLine2(line2);
				his_orbit.setTleName(orbit.getTleName());
				his_orbit.setTleEpochTime(orbit.getTleEpochTime());
				his_orbit.addPropagator("SGP4");
				his_repo.save(his_orbit);
				//更新卫星最新轨道表
				orbit.setTleLastEpochTime(orbit.getTleEpochTime());
			}
		}else {
			orbit = new LatestOrbit();
			orbit.setNoradId(noradId);
			orbit.setName(tlename);
		}
		orbit.setTleName(tlename);
		orbit.setLine1(line1);
		orbit.setLine2(line2);
		orbit.setTleEpochTime(epochTime);
		orbit.addPropagator("SGP4");
		repo.save(orbit);
	}
	
	@Override
	public void saveNewTowLineTodb(String satelId, String name, String line1, String line2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LatestOrbit getLatestOrbit(String satelId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LatestOrbit> listLatestOrbit(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<HistoryOrbit> listHistoryOrbit(String satelId, Date startTime, Date endTime, int cursor, int maxsize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HistoryOrbit getOrbit(String twolineId) {
		// TODO Auto-generated method stub
		return null;
	}

}
