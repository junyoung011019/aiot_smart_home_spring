package nsuaiot.service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    private String userId;
    private String userPassword;
    private String nickName;
    private String userName;
}
