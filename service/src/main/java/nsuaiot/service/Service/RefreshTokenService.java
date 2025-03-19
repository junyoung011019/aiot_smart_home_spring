package nsuaiot.service.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import nsuaiot.service.DTO.RefreshTokenRequest;
import nsuaiot.service.Entity.User;
import nsuaiot.service.Repository.UserRepository;
import nsuaiot.service.Security.JwtTokenGenerate;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final JwtTokenGenerate jwtTokenGenerate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> validateRefreshToken(RefreshTokenRequest refreshToken) throws IOException {
        SecretKey refreshKey = jwtTokenGenerate.getRefreshKey();

        try {
            //토큰 검증
            Claims claims = Jwts.parser()
                    .verifyWith(refreshKey)
                    .build()
                    .parseSignedClaims(refreshToken.getRefreshToken())
                    .getBody();

            String userId = claims.getSubject();

            //토큰 DB 검증
            Optional<User> refreshUser = userRepository.findByUserId(userId);
            //회원 ID로 등록된 사용자가 없을때
            if(refreshUser.isEmpty()){
                return ResponseEntity.status(401).body("Unauthorized: Cannot Found User from Database");
            }
            //DB의 토큰 값과 일치 하지 않을때
            if(!passwordEncoder.matches(refreshToken.getRefreshToken(),refreshUser.get().getRefreshToken())){
                return ResponseEntity.status(401).body("Unauthorized: refreshToken is different From Database");
            }

            String newAccessToken = jwtTokenGenerate.generateAccessToken(userId);
            JSONObject returnData = new JSONObject().put("newAccessToken", newAccessToken);
            return ResponseEntity.status(200).body(returnData.toString());

        } catch (SignatureException e){
            return ResponseEntity.status(401).body("Unauthorized: SignatureException");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid Token");
        }
    }

}
