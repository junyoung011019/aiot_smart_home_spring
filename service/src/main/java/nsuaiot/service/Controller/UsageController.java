package nsuaiot.service.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.Service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/usage")
public class UsageController {

    private final UsageService usageService;

    //주간 단일 기기 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/week/plug/{plugId}")
    public ResponseEntity<?> weekUsagePlug(@PathVariable String plugId, @RequestAttribute("userId") String userId) throws JsonProcessingException {
        return usageService.weekUsagePlug(plugId, userId);
    }

    //주간 단체 기기 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/week/groupPlug")
    public ResponseEntity<?> weekUsageGroupPlug(@RequestAttribute("userId") String userId) throws JsonProcessingException {
        return usageService.weekUsageGroupPlug(userId);
    }
    
    //주간 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/week")
    public ResponseEntity<?> weekUsage(@RequestAttribute("userId") String userId) throws JsonProcessingException{
        return usageService.weekUsage(userId);
    }

    //월별 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/month")
    public ResponseEntity<?> monthUsage(@RequestAttribute("userId") String userId) throws JsonProcessingException {
        return usageService.monthUsage(userId);
    }

}

