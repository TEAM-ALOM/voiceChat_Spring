package Alom.socialService.controller;

import Alom.login.auth.info.OAuth2UserInfo;
import Alom.socialService.service.FriendService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }
    @PostMapping("/add")
    public String addFriend(@RequestParam String friendId) {
        String currentUserId = getCurrentUserId();
        friendService.addFriend(currentUserId, friendId);
        return "Friend added succussfully";
    }
    public String removeFriend(@RequestParam String friendId){
        String currentUserId = getCurrentUserId();
        friendService.removeFriend(currentUserId,friendId);
        return "Friend removed successfully";
    }

    private String getCurrentUserId(@AuthenticationPrincipal OAuth2User oAuth2User){
        return oAuth2User.getAttributes("id");
    }
}
