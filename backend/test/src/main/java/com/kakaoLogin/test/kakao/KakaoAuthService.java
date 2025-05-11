package com.kakaoLogin.test.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Getter
@Setter
@Service
public class KakaoAuthService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}") // 환경 변수에서 API 키 로드
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") // 환경 변수에서 Redirect URI 로드
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.scope}")
    private String scope;

    public String getAccessToken(String code) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        //카카오 서버에서 x-www-form-urlencoded 이런 방식을 원하기 때문에
        //hashMap을 쓰면 application/json 이런 방식으로 나감
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        HttpHeaders headers = new HttpHeaders();
        //카카오 서버에서 원하는 방식
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params,headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        String accessToken=rootNode.get("access_token").asText();
        String refreshToken=rootNode.get("refresh_token").asText();
        String idToken=rootNode.get("id_token").asText();

        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);
        System.out.println("idToken = " + idToken);

        return getUserInfo(accessToken).toString();
    }

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public Map<String,Object> getUserInfo(String accessToken){
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 요청 (GET 방식)
        ResponseEntity<Map> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }


}
