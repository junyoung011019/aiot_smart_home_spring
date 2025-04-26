package nsuaiot.service.Entity;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "advice_log")
public class NotificationAdvice {

    @Id
    private String id;

    @Getter
    private String timestamp;

    @Getter
    private String message;
}
