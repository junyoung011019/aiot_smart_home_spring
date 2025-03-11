package com.kakaoLogin.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

}
