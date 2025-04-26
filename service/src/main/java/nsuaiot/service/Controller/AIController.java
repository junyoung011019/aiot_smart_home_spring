package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.NotificationAdvice;
import nsuaiot.service.Repository.NotificationRespository;
import nsuaiot.service.Service.AIService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIController {

    private final StringRedisTemplate stringRedisTemplate;
    private final AIService aiService;

    @Transactional(readOnly = true)
    @GetMapping("/notification")
    public ResponseEntity<?> notification() {
        return aiService.notification();
    }

    @GetMapping("/dash")
    public ResponseEntity<?> dashBoard() {
        String data = stringRedisTemplate.opsForValue().get("dash_board");
        if(data==null){
            return ResponseEntity.status(500).body("서버 에러");
        }
        return ResponseEntity.ok().body(data);
    }
}