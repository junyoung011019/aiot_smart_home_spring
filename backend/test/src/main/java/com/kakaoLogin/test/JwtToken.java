package com.kakaoLogin.test;

import io.jsonwebtoken.JwtException;
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
public class JwtToken {

    @Value("${jwt.secret.access}")
    private String ACCESS_KEY;
    private SecretKey accessKey;

    @Value("${jwt.secret.refresh}")
    private String REFRESH_KEY;
    private SecretKey refreshKey;

    @PostConstruct
    public void init(){
        byte[] accessKeyBytes = Decoders.BASE64.decode(ACCESS_KEY);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);

        byte[] refreshKeyBytes = Decoders.BASE64.decode(REFRESH_KEY);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public String generateAccessToken(String userId){
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId){
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(accessKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public boolean validateResfreshToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

}


