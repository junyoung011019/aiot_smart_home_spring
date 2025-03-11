package nsuaiot.service.Security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtTokenValid {
    private final JwtTokenGenerate jwtTokenGenerate;

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    public boolean validateToken(String token, SecretKey key) {
        if(token==null || key==null){
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true; // 검증 성공
        } catch (Exception e) {
            System.out.println("검증 실패"+e.getMessage());
            return false; // 검증 실패
        }
    }

    public Claims getClaims(String token, SecretKey key) {
        try{
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getBody();
        }catch (Exception e) {
            return null;
        }

    }

    public ResponseEntity<String> validateToken(HttpServletRequest requestHeader, boolean isAccessToken) {
        String token =getToken(requestHeader);
        if(token==null){
            return ResponseEntity.status(401).body("Unauthorized: Missing Token");
        }

        SecretKey key = isAccessToken ? jwtTokenGenerate.getAccessKey() : jwtTokenGenerate.getRefreshKey();
        if (key == null || !validateToken(token, key)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid Token");
        }

        Claims claims = getClaims(token,key);
        if(claims==null){
            return ResponseEntity.status(401).body("Unauthorized: Invalid Token Claims");
        }

        String userId=claims.getSubject();
        requestHeader.setAttribute("userId",userId);

        return null;
    }
}
