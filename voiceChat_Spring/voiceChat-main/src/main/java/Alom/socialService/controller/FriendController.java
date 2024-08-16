package Alom.socialService.controller;

import Alom.login.domain.user.User;
import Alom.login.repository.user.UserRepository;
import Alom.socialService.dto.FriendInformationDto;
import Alom.socialService.notification.NotificationService;
import Alom.socialService.service.FriendListService;
import Alom.socialService.service.FriendRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static Alom.login.util.GetCurrentUserId.getCurrentUserId;


@RestController
@RequestMapping("/friend")
public class FriendController {
    private final FriendRequestService friendRequestService;
    private final FriendListService friendListService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public FriendController(FriendRequestService friendRequestService, FriendListService friendListService, NotificationService notificationService, UserRepository userRepository) {
        this.friendRequestService = friendRequestService;
        this.friendListService = friendListService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }


    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam String friendId){
        String currentUserId = getCurrentUserId();
        String userNickname = getUserNickname(currentUserId);
        friendRequestService.sendFriendRequest(currentUserId,friendId);
        notificationService.sendFriendRequestNotification(friendId,"You have new friend request from "+userNickname);
        return "Friend request send succesfully";
    }

    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam String requesterId){
        String currentUserId = getCurrentUserId();
        friendRequestService.acceptFriendRequest(currentUserId,requesterId);
        return "Friend request accepted successfully";
    }

    @GetMapping("/requestList")
    public List<String> getPendingFriendRequest(){
        String currentUserId = getCurrentUserId();
        return friendRequestService.getPendingFriendRequest(currentUserId)
                .stream()
                .map(User::getUserProviderId)
                .collect(Collectors.toList());
    }

    @GetMapping("/friendList")
    public List<FriendInformationDto> getFriends(){
        String currentUserId = getCurrentUserId();
        return friendListService.getFriendList(currentUserId);
    }

    private String getUserNickname(String userId){
        User user = userRepository.findByProviderId(userId);
        return user.getUserNickname();
    }


}
