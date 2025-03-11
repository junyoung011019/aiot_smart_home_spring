package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.UserLoginDTO;
import nsuaiot.service.DTO.UserRegisterDTO;
import nsuaiot.service.DTO.UserTokenDTO;
import nsuaiot.service.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/userIdExists")
    public ResponseEntity<String> userIdExists(@RequestParam String userId) {
        return userService.userIdExists(userId);
    }

    @GetMapping("/nicknameExists")
    public ResponseEntity<String> nicknameExists(@RequestParam String nickName) {
        return userService.nickNameExists(nickName);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }
}
