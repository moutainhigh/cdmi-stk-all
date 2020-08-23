package pw.cdmi.starlink.orbit.updater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import pw.cdmi.starlink.orbit.updater.services.OrbitUpdaterService;

@SpringBootApplication
@EnableScheduling
public class StarlinkOrbitUpdaterApplication {
	
	@Autowired
	private OrbitUpdaterService service;
	
	@Value("${updater.config.towline.httpfile.url}")
    private String httpfile;
	
	public static void main(String[] args) {
		SpringApplication.run(StarlinkOrbitUpdaterApplication.class, args);
	}
	
	@Scheduled(cron = "0 0/1 * * * ?") //每分钟启动
	//@Scheduled(cron = "0 0 4 * * ?")//凌晨两点执行
	public void refreshTwoLine(){
		service.readTowLineFileFromRemote(httpfile);
	}

}
