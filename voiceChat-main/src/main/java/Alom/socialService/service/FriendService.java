package Alom.socialService.service;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

public interface FriendService {
    @Transactional
    void addFriend(String userId,String friendId);
    @Transactional
    void removeFriend(String userId,String friendId);
}
