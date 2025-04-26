package nsuaiot.service.Repository;

import nsuaiot.service.Entity.Usage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface UsageRepository extends MongoRepository<Usage,String> {
    List<Usage> findByPlugIdAndUsageDateBetween(String plugId, Date start, Date end);
}
