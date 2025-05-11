package nsuaiot.service.Repository;


import nsuaiot.service.Entity.Plug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlugRepository extends JpaRepository<Plug, Integer> {
    Optional<Plug> findByPlugIdAndOwnerId(String plugId,String ownerId);
    boolean existsByPlugId(String plugId);
    Optional<List<Plug>> findByOwnerId(String ownerId);

    Plug findByPlugId(String plugId);
}
