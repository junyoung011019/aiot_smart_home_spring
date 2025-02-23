package nsuaiot.service.Repository;


import nsuaiot.service.Entity.GroupPlugManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupPlugManagementRepository extends JpaRepository<GroupPlugManagement, Long> {
    Optional<GroupPlugManagement> findByGroupIdAndPlugId(Long groupId, String plugId);

    List<GroupPlugManagement> findByGroupId(Long groupId);
}


