package nsuaiot.service.Repository;

import nsuaiot.service.Entity.GroupList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupListRepository extends JpaRepository<GroupList, Long> {
    Optional<List<GroupList>> findByOwnerId(String ownerId);
    boolean existsByOwnerIdAndGroupName(String ownerId, String groupName);
    Optional<GroupList> findByGroupIdAndOwnerId(Long groupId, String ownerId);
}