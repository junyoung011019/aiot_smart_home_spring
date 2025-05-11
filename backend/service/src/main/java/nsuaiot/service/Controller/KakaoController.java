package nsuaiot.service.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
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

    //플러터 및 빅스비 캡슐 콜백
    @Transactional(readOnly = false)
    @GetMapping("/login")
    public ResponseEntity<?> flutterCallback(@RequestParam String accessToken, String fcmKey) throws JsonProcessingException {
        String kakaoUserId = kakaoService.getUserInfo(accessToken);
        return kakaoService.getUserByKakaoUserId(kakaoUserId, fcmKey);
    }


}
