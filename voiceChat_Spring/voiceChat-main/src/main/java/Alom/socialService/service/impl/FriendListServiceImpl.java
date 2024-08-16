package Alom.socialService.service.impl;

import Alom.login.domain.user.User;
import Alom.login.repository.user.UserRepository;
import Alom.login.util.GetCurrentUserId;
import Alom.socialService.domain.FriendRequest;
import Alom.socialService.dto.FriendInformationDto;
import Alom.socialService.repository.FriendRequestRepository;
import Alom.socialService.service.FriendListService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Alom.login.util.GetCurrentUserId.getCurrentUserId;

public class FriendListServiceImpl implements FriendListService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendListServiceImpl(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    @Override
    public List<FriendInformationDto> getFriendList(String currentUserId) {
        User user = userRepository.findByProviderId(currentUserId);
        List<FriendRequest> friendAsRequester = friendRequestRepository.findAllByRequestFromUserAndAcceptedByReceiverAndAcceptedByRequester(user,true,true);
        List<FriendRequest> friendAsReceiver = friendRequestRepository.findAllByRequestToUserAndAcceptedByReceiverAndAcceptedByRequester(user,true,true);

        List<FriendInformationDto> friends = new ArrayList<>();
        friends.addAll(friendAsRequester.stream().map(friendRequest -> new FriendInformationDto(
                friendRequest.getRequestToUser().getUserName(),
                friendRequest.getRequestToUser().getUserNickname(),
                friendRequest.getRequestToUser().getUserMent(),
                friendRequest.getRequestToUser().getUserIconPath()
        )).toList());
        friends.addAll(friendAsReceiver.stream().map(friendRequest -> new FriendInformationDto(
                friendRequest.getRequestFromUser().getUserName(),
                friendRequest.getRequestFromUser().getUserNickname(),
                friendRequest.getRequestFromUser().getUserMent(),
                friendRequest.getRequestFromUser().getUserIconPath()
        )).toList());
        return friends;
    }
}
