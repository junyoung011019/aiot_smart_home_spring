package nsuaiot.service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
public class ApiResponse {

    private String message;
}
