package nsuaiot.service.Controller.Usage;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Service.UsageWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/usage/week")
public class UsageWeekController {

    private final UsageWeekService usageWeekService;
    //주간 단체 기기 사용량 조회 : 각 기기별의 전력량 합 (이번주 사용량 ~7일)
    //주간 단일 기기 사용량 조회 : 단일 기기의 이번주(각 날짜) 전력량(보내는 날의 월~일)

//    @GetMapping("/groupPlug")
//    public ResponseEntity<String> usageGroupPlug(){
//
//    }

    //id=아이디 값, 시작 날짜 , 끝 날짜
    @GetMapping("/plug/{plugId}")
    public ResponseEntity<String> usagePlug(@PathVariable String plugId){
        return usageWeekService.weekUsagePlug(plugId);
    }

}

