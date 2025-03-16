package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.DTO.UserLoginDTO;
import nsuaiot.service.DTO.UserRegisterDTO;
import nsuaiot.service.DTO.UserTokenDTO;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import nsuaiot.service.Security.JwtTokenGenerate;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerate jwtTokenGenerate;

    //아이디 중복 체크
    public ResponseEntity<String> userIdExists(String userId){
        Optional<User> findIdExists =userRepository.findByUserId(userId);
        if(!findIdExists.isPresent()){
            return ResponseEntity.status(204).body("아이디 사용 가능합니다.");
        }else{
            return ResponseEntity.status(409).body("이미 사용되는 아이디입니다.");
        }
    }

    //닉네임 중복 체크
    public ResponseEntity<String> nickNameExists(String nickName){
        Optional<User> findNickNameExists =userRepository.findByNickName(nickName);
        if(!findNickNameExists.isPresent()){
            return ResponseEntity.status(204).body("닉네임 사용 가능합니다.");
        }else{
            return ResponseEntity.status(409).body("이미 사용되는 닉네임입니다.");
        }
    }

    //회원가입
    public ResponseEntity<?> register(UserRegisterDTO userRegisterDTO){
        String registerNickNameName=userRegisterDTO.getNickName();
        String registerUserId=userRegisterDTO.getUserId();
        String registerUserPW=userRegisterDTO.getUserPassword();

        // ID & PW 조건 체크 / 중복 가입 체크
        if(!(registerUserId.matches("^[a-zA-Z0-9]{5,10}$"))){
            return ResponseEntity.status(400).body(new ApiResponse("아이디는 5자리 이상, 10자리 이하여야합니다."));
        }
        if(!(registerUserPW.matches("^[a-zA-Z0-9]{8,20}$"))){
            return ResponseEntity.status(400).body(new ApiResponse("비밀번호는 8자리 이상, 20자리 이하여야합니다."));
        }
        if(userRepository.findByUserIdOrNickName(registerUserId, registerNickNameName).isPresent()){
            return ResponseEntity.status(409).body(new ApiResponse("아이디 혹은 닉네임이 등록되어 있습니다."));
        }

        User user = new User();
        user.setUserName(userRegisterDTO.getUserName());
        user.setUserId(registerUserId);
        user.setNickName(registerNickNameName);
        user.setUserPassword(passwordEncoder.encode(registerUserPW));

        userRepository.save(user);
        return ResponseEntity.ok().body(new ApiResponse("회원가입이 완료되었습니다."));
    }

    //로그인
    public ResponseEntity<?> login(UserLoginDTO userLoginDTO){
        Optional<User> findUser= userRepository.findByUserId(userLoginDTO.getUserId());
        if(findUser.isPresent()&&passwordEncoder.matches(userLoginDTO.getUserPassword(),findUser.get().getUserPassword())){
            //로그인 성공시 userId로 (액세스/리프레시) 토큰 발급
            String userId = userLoginDTO.getUserId();
            String accessToken = jwtTokenGenerate.generateAccessToken(userId);
            String refreshToken = jwtTokenGenerate.generateRefreshToken(userId);

            return ResponseEntity.status(200).body(new UserTokenDTO(accessToken,refreshToken));
        }else{
            return ResponseEntity.status(401).body(new ApiResponse("아이디 혹은 비밀번호가 옳지 않습니다"));
        }
    }
}
