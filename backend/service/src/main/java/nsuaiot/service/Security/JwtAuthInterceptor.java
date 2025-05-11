package nsuaiot.service.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {
    private final JwtTokenGenerate jwtTokenGenerate;
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/user/register", "/user/login", "/user/userIdExists", "/user/nickNameExists",
            "/kakao/callback", "/kakao/flutter",
            "/bixby/login", "/bixby/login/kakao", "/token/refresh", "error", "/token/bixby/refresh"
    );

    //토큰 값만 필터링
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    //요청에 대한 토큰 인터셉트
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);

        //요청에 토큰이 없을때
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing Token");
            return false;
        }

        String requestURI = request.getServletPath();
        //예외 주소일때
        if (EXCLUDED_PATHS.contains(requestURI)) {
            return true;
        }

        SecretKey key = jwtTokenGenerate.getAccessKey();

        //키 오류 일때
        if (key == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Key Not Found");
            return false;
        }

        //키 인증
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getBody();

            String userId = claims.getSubject();
            request.setAttribute("userId", userId);
            return true;

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid Token - 요청에 대한 토큰이 인가되지 않았습니다.");
            return false;
        }
    }



}
