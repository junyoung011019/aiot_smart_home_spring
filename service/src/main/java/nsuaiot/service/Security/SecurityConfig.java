package nsuaiot.service.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //일시 해제
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        //로그인 페이지와 메인 페이지는 누구든 가능
//                        .requestMatchers("/", "tokenTest","/kakao/token","kakao/login-url","/kakao/callback", "/login", "/static/**","register").permitAll()  // 로그인 페이지 허용
//                        //그 외 페이지는 ㄴㄴ
//                        .anyRequest().authenticated()
                                //지금은 모든 요청 허용
                                .anyRequest().permitAll()
                )
                //oauth 로그인 ㅇㅋ
                .oauth2Login(oauth2 -> oauth2
                        //login 방문하면 로그인 페이지 보여주셈
                        .loginPage("/login")
                        //로그인 되면 home으로 ㄱㄱ
                        .defaultSuccessUrl("/home", true)
                );
        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // 허용할 Origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }

}
