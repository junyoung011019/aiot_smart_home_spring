package nsuaiot.service.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.DTO.UserTokenDTO;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import nsuaiot.service.Security.JwtTokenGenerate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtTokenGenerate jwtTokenGenerate;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}") // 환경 변수에서 API 키 로드
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") // 환경 변수에서 Redirect URI 로드
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.scope}")
    private String scope;

    //카카오 (인가코드 -> 액세스 토큰)
    public String getToken(String authorizationCode) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        //카카오 서버에서 x-www-form-urlencoded 이런 방식을 원하기 때문에
        //hashMap을 쓰면 application/json 이런 방식으로 나감
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", authorizationCode);
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

        return accessToken;
    }


    //카카오 (액세스 토큰 -> 사용자 아이디 조회)
    public String getUserInfo(String accessToken){
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        //발급 받은 토큰 헤더에 주입
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 요청 (GET 방식)
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );
        String kakaoId = response.getBody().get("id").toString();
        return kakaoId;
    }


    //카카오 ID -> DB 조회 -> 리다이렉트 URL 전달 (빅스비 캡슐)
    public ResponseEntity<?> getRedirectURL(String kakaoUserId) {
        Optional<User> findUser = userRepository.findByKakaoId(kakaoUserId);
        if (findUser.isEmpty()) {
            return ResponseEntity.status(401).body(new ApiResponse("카카오 아이디 : " + kakaoUserId + " 등록된 회원이 없습니다."));
        }
        String userId = findUser.get().getUserId();
        String accessToken = jwtTokenGenerate.generateAccessToken(userId);
        String refreshToken = jwtTokenGenerate.generateRefreshToken(userId);

        //리프레시 토큰 해싱 후 DB 저장
        String hashedRefreshToken = passwordEncoder.encode(refreshToken);
        findUser.get().setRefreshToken(hashedRefreshToken);
        userRepository.save(findUser.get());

        String vivAppUrl = String.format(
                "viv-app://authentication/?intent=LoginOAuth&accessToken=%s&refreshToken=%s",
                accessToken, refreshToken
        );

        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(vivAppUrl))
                .build();
    }

    //카카오 ID -> DB 조회 -> 토큰 발행 (앱)
    public ResponseEntity<?> getUserByKakaoUserId(String kakaoUserId){
        Optional<User> findUser = userRepository.findByKakaoId(kakaoUserId);
        if(findUser.isEmpty()){
            return ResponseEntity.status(401).body(new ApiResponse("카카오 아이디 : "+ kakaoUserId +" 등록된 회원이 없습니다."));
        }
        String userId = findUser.get().getUserId();
        String accessToken = jwtTokenGenerate.generateAccessToken(userId);
        String refreshToken = jwtTokenGenerate.generateRefreshToken(userId);

        //리프레시 토큰 해싱 후 DB 저장
        String hashedRefreshToken = passwordEncoder.encode(refreshToken);
        findUser.get().setRefreshToken(hashedRefreshToken);
        userRepository.save(findUser.get());

        return ResponseEntity.status(200).body(new UserTokenDTO(accessToken,refreshToken));
    }
}
