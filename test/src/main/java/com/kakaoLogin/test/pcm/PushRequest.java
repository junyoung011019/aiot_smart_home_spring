package com.kakaoLogin.test.pcm;

import lombok.Data;

@Data
class PushRequest {
    private String token;
    private String title;
    private String body;
}
