package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.RefreshTokenRequest;
import nsuaiot.service.Service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/token")
public class TokenController {

        private final RefreshTokenService refreshTokenService;

        //리프레시 토큰을 통한 액세스 토큰
        @Transactional(readOnly = true)
        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshToken) throws IOException {
                return refreshTokenService.validateRefreshToken(refreshToken);
        }

        //리프레시 토큰을 통한 액세스 토큰 (빅스비 캡슐)
        @Transactional(readOnly = true)
        @PostMapping("/bixby/refresh")
        public ResponseEntity<?> bixbyRefreshToken(@RequestBody RefreshTokenRequest refreshToken) throws IOException {
                ResponseEntity<?> data = refreshTokenService.validateRefreshToken(refreshToken);
                if(data.getStatusCode().equals(200)){
                        String newAccessToken = data.toString();
                        String vivAppUrl = String.format(
                                "viv-app://authentication/?intent=LoginOAuth&accessToken=%s&newRefreshToken=%s",
                                newAccessToken
                        );
                        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                                .location(URI.create(vivAppUrl))
                                .build();
                }else{
                        return data;
                }
        }

}
