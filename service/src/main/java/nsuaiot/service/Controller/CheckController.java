package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.PlugActionRequest;
import nsuaiot.service.Service.CheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/check")
public class CheckController {

    private final CheckService checkService;

    @GetMapping("/plugList")
    public ResponseEntity<String> checkPlugList(){
        return checkService.checkPlugList();
    }

    @GetMapping("/plugState/{plugId}")
    public ResponseEntity<String> checkPlug(@PathVariable String plugId){
        return checkService.checkPlug(plugId);
    }

}
