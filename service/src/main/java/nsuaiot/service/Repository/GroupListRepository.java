package nsuaiot.service.Repository;

import nsuaiot.service.Entity.GroupList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupListRepository extends JpaRepository<GroupList, Long> {
    boolean existsByGroupName(String groupName);
}