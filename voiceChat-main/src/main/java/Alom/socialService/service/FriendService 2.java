package Alom.socialService.service;

import Alom.login.domain.user.User;
import Alom.login.repository.user.UserRepository;
import Alom.socialService.repository.FriendRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public FriendService(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    @Transactional
    public void addFriend(String userEmail,String friendEmail){
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(()->new IllegalArgumentException("User not found"));
        User friend = userRepository.findByUserEmail(friendEmail)
                .orElseThrow(()->new IllegalArgumentException("Friend not found"));
    }
    if(friendRepository.existsByUserAndFriend(user,friend)){
        throw new IllegalArgumentException("Already friends");
    }
    }


}
