package nsuaiot.service.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "*")
public class TestController {
    @GetMapping("/")
    public String hello(){
        return "ㅎㅇㅎㅇ";
    }
}
