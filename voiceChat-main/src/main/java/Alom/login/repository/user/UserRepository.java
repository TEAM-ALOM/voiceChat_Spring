package Alom.login.repository.user;
import Alom.login.domain.user.User;
import Alom.socialService.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.userUuid = :userUuid")
    Optional<User> findByUser(UUID userUuid);
    @Query("SELECT u FROM User u WHERE u.userEmail = :userEmail")
    Optional<User> findByUserEmail(String userEmail);
    User findByProviderId(String providerId);
}
