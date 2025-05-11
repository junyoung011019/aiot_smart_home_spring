package com.kakaoLogin.test.pcm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class NotificationController {

    private final FCMService fcmService;

    public NotificationController(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping
    public ResponseEntity<String> sendPush(@RequestBody PushRequest request) {
        fcmService.sendNotification(request.getToken(), request.getTitle(), request.getBody());
        return ResponseEntity.ok("알림 전송 완료");
    }
}

