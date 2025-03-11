package com.kakaoLogin.test.user;

import com.kakaoLogin.test.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtToken jwtToken;

    public ResponseEntity<String> register(UserDto userDto){
        User user= new User();
        user.setUserName(userDto.getUserName());
        user.setUserId(userDto.getUserId());
        String userPw = userDto.getUserPassword();
        String encodingPw = passwordEncoder.encode(userPw);
        user.setUserPassword(encodingPw);

        userRepository.save(user);

        return ResponseEntity.ok(" ");
    }

    public ResponseEntity<String> login(UserLoginDto userLoginDto){
        Optional<User> loginUser = userRepository.findAllByUserId(userLoginDto.getUserId());
        if(!loginUser.isPresent()){
            return ResponseEntity.status(404).body("not User");
        }
        if(passwordEncoder.matches(userLoginDto.getUserPassword(),loginUser.get().getUserPassword())){
            return ResponseEntity.status(200).body(jwtToken.generateAccessToken(userLoginDto.getUserId()));
        }else{
            return ResponseEntity.status(400).body("not Login");
        }
    }
}
