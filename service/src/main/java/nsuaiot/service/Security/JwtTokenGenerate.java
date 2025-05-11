package nsuaiot.service.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenGenerate {

    // (액세스/리프레시) 암호키 주입
    @Value("${jwt.secret.access}")
    private String ACCESS_KEY;
    private SecretKey accessKey;

    @Value("${jwt.secret.refresh}")
    private String REFRESH_KEY;
    private SecretKey refreshKey;

    @PostConstruct
    public void init(){
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_KEY));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_KEY));
    }

    // (액세스/리프레시) 키 생성
    public String generateAccessToken(String userId){
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                //1시간
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId){
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                //7일
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24*7))
                .signWith(refreshKey, Jwts.SIG.HS256)
                .compact();
    }

    //생성된 (액세스/리프레시) 키 리턴
    public SecretKey getAccessKey(){
        return accessKey;
    }
    public SecretKey getRefreshKey(){
        return refreshKey;
    }
    
}
