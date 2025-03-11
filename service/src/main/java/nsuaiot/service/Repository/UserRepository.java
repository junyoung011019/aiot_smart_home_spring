package nsuaiot.service.Repository;

import nsuaiot.service.DTO.UserLoginDTO;
import nsuaiot.service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserIdOrNickName(String userId, String nickName);

    Optional<User> findByUserId(String userId);

    Optional<User> findByNickName(String NickName);
}
