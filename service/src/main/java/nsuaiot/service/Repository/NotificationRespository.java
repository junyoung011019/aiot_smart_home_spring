package nsuaiot.service.Repository;

import nsuaiot.service.Entity.NotificationAdvice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface NotificationRespository extends MongoRepository<NotificationAdvice, String> {
}
