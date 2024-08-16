package Alom.socialService.service;

import Alom.login.domain.user.User;
import Alom.socialService.dto.FriendInformationDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FriendListService {
    @Transactional(readOnly = true)
    public List<FriendInformationDto> getFriendList(String currentUserId);
}
