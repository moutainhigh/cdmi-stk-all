package pw.cdmi.starlink.orbit.updater.rs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orbit/updater/v1/task")
public class UpdaterTaskResource {
	
	
	//暂停计划的执行
    @GetMapping("/pause")
    public void pause() {

    }

    //恢复计划的执行
    @GetMapping("/resume")
    public void resume() {

    }
}
