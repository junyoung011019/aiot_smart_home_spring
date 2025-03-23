package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/bixby")
public class BixbyController {

    @Value("${spring.security.oauth2.client.registration.kakao.javascript-key}")
    private String kakaoJsKey;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/login/kakao")
    public String getKakaoConfig(){
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoJsKey
                + "&redirect_uri=" + kakaoRedirectUri
                + "&response_type=code";
        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping("/legal/terms")
    public String legalTerms(){
        return "terms";
    }

    @GetMapping("/legal/privacy")
    public String legalPrivacy(){
        return "privacy";
    }



}
