package Alom.socialService.service.impl;

import Alom.login.domain.user.User;
import Alom.login.repository.user.UserRepository;
import Alom.socialService.repository.FriendRepository;
import Alom.socialService.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;


    @Override
    public void addFriend(String userId, String friendId) {
        User user = userRepository.findByUserEmail(userId)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        User friend = userRepository.findByUserEmail(friendId)
                .orElseThrow(()-> new IllegalArgumentException("Friend not found"));
        if (friendRepository.existsByFriendUserIdAndFriendFriendId(user,friend)){
            throw new IllegalArgumentException("Already friends");
        }
        user.addFriend(friend);
        userRepository.save(user);
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        User user = userRepository.findByUserEmail(userId)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        User friend = userRepository.findByUserEmail(friendId)
                .orElseThrow(()-> new IllegalArgumentException("Friend not found"));
        user.removeFriend(friend);
        userRepository.save(user);
    }
}
