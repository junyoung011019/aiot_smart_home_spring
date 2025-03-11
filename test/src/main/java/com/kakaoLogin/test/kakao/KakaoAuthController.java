package com.kakaoLogin.test.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/kakao/login-url")
    public String getKakaoLoginUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoAuthService.getKakaoClientId()
                + "&redirect_uri=" + kakaoAuthService.getKakaoRedirectUri()
                + "&response_type=code";

        return kakaoAuthUrl;
    }
}
