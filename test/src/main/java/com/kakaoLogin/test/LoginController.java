package com.kakaoLogin.test;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kakaoLogin.test.kakao.KakaoAuthService;
import com.kakaoLogin.test.user.UserDto;
import com.kakaoLogin.test.user.UserLoginDto;
import com.kakaoLogin.test.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LoginController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final JwtToken jwtToken;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto){
        return userService.register(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto){
        return userService.login(userLoginDto);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) throws JsonProcessingException {
        String accessTokenResponse = kakaoAuthService.getAccessToken(code);
        System.out.println(accessTokenResponse);
        return ResponseEntity.ok(accessTokenResponse);
    }

    @GetMapping("/tokenTest")
    public ResponseEntity<String> tokenTest(@RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Invalid or missing Authorization header");
        }

        String token = authHeader.substring(7);  // "Bearer " 이후의 값만 가져옴

        // ✅ 토큰 검증
        if (jwtToken.validateAccessToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

}
