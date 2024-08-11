package Alom.socialService.repository;

import Alom.login.domain.user.User;
import Alom.socialService.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface FriendRepository extends JpaRepository<Friend,Long> {
    boolean existsByFriendUserIdAndFriendFriendId(User user,User friend);


}
