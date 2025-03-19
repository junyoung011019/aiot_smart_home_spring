package nsuaiot.service.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")
        .excludePathPatterns(
                //UserController
                "/user/register",
                "/user/login",
                "/user/userIdExists",
                "/user/nickNameExists",

                //KakaoController
                "/kakao/callback",
                "/kakao/flutter",

                //BixbyController
                "/bixby/login",
                "/bixby/login/kakao",

                //TokenController
                "/token/refresh",
                "/token/bixby/refresh",

                "/error"
        );
    }

}
