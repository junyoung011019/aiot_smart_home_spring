package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.UserLoginDTO;
import nsuaiot.service.DTO.UserRegisterDTO;
import nsuaiot.service.DTO.UserTokenDTO;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import nsuaiot.service.Security.JwtTokenGenerate;
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

    public ResponseEntity<String> userIdExists(String userId){
        Optional<User> findIdExists =userRepository.findByUserId(userId);
        if(!findIdExists.isPresent()){
            return ResponseEntity.status(200).body(" ");
        }else{
            return ResponseEntity.status(400).body("이미 사용되는 아이디입니다.");
        }
    }

    public ResponseEntity<String> nickNameExists(String nickName){
        Optional<User> findNickNameExists =userRepository.findByNickName(nickName);
        if(!findNickNameExists.isPresent()){
            return ResponseEntity.status(200).body(" ");
        }else{
            return ResponseEntity.status(400).body("이미 사용되는 닉네임입니다.");
        }
    }

    public ResponseEntity<String> register(UserRegisterDTO userRegisterDTO){
        User user = new User();
        String registerNickNameName=userRegisterDTO.getNickName();
        String registerUserId=userRegisterDTO.getUserId();

        if(!(registerUserId.length() >=5 && registerUserId.length()<=10)){
            return ResponseEntity.status(400).body("아이디는 5자리 이상, 10자리 이하여야합니다.");
        }

        if(!(userRegisterDTO.getUserPassword().length()>7 && userRegisterDTO.getUserPassword().length()<21)){
            return ResponseEntity.status(400).body("비밀번호는 8자리 이상, 20자리 이하여야합니다.");
        }

        Optional<User> checkIsUser = userRepository.findByUserIdOrNickName(registerUserId, registerNickNameName);

        if(checkIsUser.isPresent()){
            return ResponseEntity.status(400).body("아이디 혹은 닉네임이 등록되어 있습니다.");
        }

        user.setUserName(userRegisterDTO.getUserName());
        user.setUserId(registerUserId);
        user.setNickName(registerNickNameName);
        user.setUserPassword(passwordEncoder.encode(userRegisterDTO.getUserPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(" ");
    }

    public ResponseEntity<?> login(UserLoginDTO userLoginDTO){
        Optional<User> findUser= userRepository.findByUserId(userLoginDTO.getUserId());
        if(findUser.isPresent()&&passwordEncoder.matches(userLoginDTO.getUserPassword(),findUser.get().getUserPassword())){
            String accessToken = jwtTokenGenerate.generateAccessToken(userLoginDTO.getUserId());
            String refreshToken = jwtTokenGenerate.generateRefreshToken(userLoginDTO.getUserId());

            return ResponseEntity.status(200).body(new UserTokenDTO(accessToken,refreshToken));
        }else{
            return ResponseEntity.status(400).body("아이디 혹은 비밀번호가 옳지 않습니다");
        }
    }
}
