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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtTokenGenerate jwtTokenGenerate;
    private final PasswordEncoder passwordEncoder;

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

    //카카오 ID -> DB 조회 -> 토큰 발행 (앱)
    public ResponseEntity<?> getUserByKakaoUserId(String kakaoUserId, String fcmKey){
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
        if(fcmKey!=null){
            findUser.get().setFcmKey(fcmKey);
        }
        userRepository.save(findUser.get());

        return ResponseEntity.status(200).body(new UserTokenDTO(accessToken,refreshToken,findUser.get().getNickName()));
    }
}
