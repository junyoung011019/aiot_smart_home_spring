package nsuaiot.service.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.UserTokenDTO;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import nsuaiot.service.Service.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    //카카오 인가코드 -> 사용자 조회(DB 조회) -> 토큰 발급
    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) throws JsonProcessingException {
        //카카오 (인가코드 -> 액세스 토큰)
        String accessToken = kakaoService.getToken(code);
        //카카오 (액세스 토큰 -> 사용자 아이디 조회)
        String kakaoUserId = kakaoService.getUserInfo(accessToken);
        return kakaoService.getRedirectURL(kakaoUserId);
    }

    //플러터용 콜백
    @Transactional(readOnly = true)
    @GetMapping("/flutter")
    public ResponseEntity<?> flutterCallback(@RequestParam String accessToken) throws JsonProcessingException {
        String kakaoUserId = kakaoService.getUserInfo(accessToken);
        return kakaoService.getUserByKakaoUserId(kakaoUserId);
    }


}
