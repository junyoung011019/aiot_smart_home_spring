package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.PlugActionRequest;
import nsuaiot.service.Repository.PlugRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ControlController {

    private final PlugRepository plugRepository;

    @GetMapping("/control/device/{plugId}")
    public String controlDevice(@PathVariable Integer plugId, @RequestBody PlugActionRequest plugActionRequest){
        String action = plugActionRequest.getAction();

        if("on".equals(action)){

        }else if("off".equals(action)){

        }else{
            return "잘못된 요청입니다.";
        }
    }
}
