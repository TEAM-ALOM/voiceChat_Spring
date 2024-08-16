package Alom.socialService.repository;

import Alom.login.domain.user.User;
import Alom.socialService.domain.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {
    Optional<FriendRequest> findByRequestFromUserAndRequestToUser(User fromUser,User toUser);
    List<FriendRequest> findAllByRequestToUserAndAcceptedByReceiver(User toUser,boolean acceptedByReceiver);
    List<FriendRequest> findAllByRequestFromUserAndAcceptedByReceiverAndAcceptedByRequester(User requester,boolean acceptedByRequester,boolean acceptedByReceiver);
    List<FriendRequest> findAllByRequestToUserAndAcceptedByReceiverAndAcceptedByRequester(User receiver,boolean acceptedByRequester,boolean acceptedByReceiver);

}
