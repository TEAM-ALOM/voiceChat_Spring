package Alom.login.domain.user;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
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

    @Column(name = "user_icon_path",nullable = true)
    private String userIconPath;

    @Column(nullable = true)
    private String userMent;

    @ElementCollection
    @Column(nullable = true)
    private Set<String> userBlockList = new HashSet<>();


}
