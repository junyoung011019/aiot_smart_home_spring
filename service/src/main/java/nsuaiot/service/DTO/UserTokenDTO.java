package nsuaiot.service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTokenDTO {
    private String accessToken;
    private String refreshToken;
    private String userNickName;

    public UserTokenDTO(String accessToken, String refreshToken, String userNickName){
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
        this.userNickName=userNickName;
    }
}
