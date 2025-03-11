package nsuaiot.service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTokenDTO {
    private String accessToken;
    private String refreshToken;

    public UserTokenDTO(String accessToken, String refreshToken){
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
    }
}
