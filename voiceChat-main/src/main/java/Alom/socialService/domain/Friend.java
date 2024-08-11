package Alom.socialService.domain;

import Alom.login.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name="friend")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long friendId;

    @ManyToOne
    @JoinColumn(name = "friend_user_id")
    private User friendUserId;

    @ManyToOne
    @JoinColumn(name = "friend_friend_id")
    private User friendFriendId;

    public Friend(User user,User friend){
        this.friendUserId = user;
        this.friendFriendId = friend;
    }

    public User getFriend(){
        return friendFriendId;
    }

}
