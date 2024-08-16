package Alom.socialService.domain;

import Alom.login.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="request")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "request_from_id")
    private User requestFromUser;

    @ManyToOne
    @JoinColumn(name = "request_to_id")
    private User requestToUser;

    private boolean acceptedByRequester;
    private boolean acceptedByReceiver;

    public boolean isAccepted(){
        return acceptedByReceiver&&acceptedByRequester;
    }

}
