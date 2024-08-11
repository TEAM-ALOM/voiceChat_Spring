package Alom.login.domain.user;

import Alom.socialService.domain.Friend;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name="user_uuid",columnDefinition = "BINARY(16)",unique=true)
    private UUID userUuid;

    @Column(name="user_provider",nullable = false,length = 10)
    private String userProvider;

    @Column(name="user_provider_id",nullable = false,length = 50)
    private String userProviderId;

    @Column(name = "user_name",nullable = false, length = 10)
    private String userName;

    @Column(name = "user_nickname",nullable = false,length = 10)
    private String userNickname;

    @CreationTimestamp
    @Column(name = "user_create_date",nullable = false,length = 20)
    private LocalDateTime userCreateDate;

    @UpdateTimestamp
    @Column(name = "user_update_date",nullable = false,length = 20)
    private LocalDateTime userUpdateDate;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] userIcon;

    @Column(nullable = true)
    private String userMent;

    @ElementCollection
    @Column(nullable = true)
    private Set<String> userBlockList = new HashSet<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private  Set<Friend> friends = new HashSet<>();

    public void addFriend(User friend){
        Friend friendship = new Friend(this,friend);
        friends.add(friendship);
    }
    public void removeFriend(User friend){
        friends.removeIf(f -> f.getFriend().equals(friend));
    }
}
