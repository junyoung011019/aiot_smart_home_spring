package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.RefreshTokenRequest;
import nsuaiot.service.Service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

}
