package nsuaiot.service.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import nsuaiot.service.Service.UsageService;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/usage")
public class UsageController {

    private final UsageService usageService;

    //주간 단일 기기 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/week/plug/{plugId}")
    public ResponseEntity<?> weekUsagePlug(@PathVariable String plugId, @RequestAttribute("userId") String userId) throws IOException, InterruptedException {
        return usageService.weekUsagePlug(plugId, userId);
    }

    //주간 단체 기기 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/week/groupPlug")
    public ResponseEntity<?> weekUsageGroupPlug(@RequestAttribute("userId") String userId) throws IOException, InterruptedException {
        return usageService.weekUsageGroupPlug(userId);
    }

    //주간 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/week")
    public ResponseEntity<?> weekUsage(@RequestAttribute("userId") String userId) throws IOException, InterruptedException {
        return usageService.weekUsage(userId);
    }


    //월별 사용량 조회
    @Transactional(readOnly = true)
    @GetMapping("/month")
    public ResponseEntity<?> monthUsage(@RequestAttribute("userId") String userId) throws IOException, InterruptedException {
        return usageService.monthUsage(userId);
    }

}

