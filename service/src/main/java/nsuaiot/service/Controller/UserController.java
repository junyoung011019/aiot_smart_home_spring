package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.UserLoginDTO;
import nsuaiot.service.DTO.UserRegisterDTO;
import nsuaiot.service.DTO.UserTokenDTO;
import nsuaiot.service.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    //아이디 중복 체크
    @Transactional(readOnly = true)
    @GetMapping("/userIdExists")
    public ResponseEntity<?> userIdExists(@RequestParam String userId) {
        return userService.userIdExists(userId);
    }

    //닉네임 중복 체크
    @Transactional(readOnly = true)
    @GetMapping("/nickNameExists")
    public ResponseEntity<?> nicknameExists(@RequestParam String nickName) {
        return userService.nickNameExists(nickName);
    }

    //회원가입
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }
    
    //로그인
    @Transactional(readOnly = false)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }
}
