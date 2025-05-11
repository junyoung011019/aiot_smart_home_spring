package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Service.CheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/check")
public class CheckController {

    private final CheckService checkService;

    //단일 플러그 조회
    @Transactional(readOnly = true)
    @GetMapping("/plugState/{plugId}")
    public ResponseEntity<?> checkPlug(@PathVariable String plugId, @RequestAttribute("userId") String userId){
        return checkService.checkPlug(plugId, userId);
    }

    //전체 플러그 조회
    @Transactional(readOnly = true)
    @GetMapping("/plugList")
    public ResponseEntity<?> checkPlugList(@RequestAttribute("userId") String userId){
        return checkService.checkPlugList(userId);
    }




}
