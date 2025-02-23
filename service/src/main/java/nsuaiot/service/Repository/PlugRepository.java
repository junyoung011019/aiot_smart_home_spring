package nsuaiot.service.Repository;


import nsuaiot.service.Entity.Plug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlugRepository extends JpaRepository<Plug, Integer> {
    Optional<Plug> findByPlugId(String plugId);
}
