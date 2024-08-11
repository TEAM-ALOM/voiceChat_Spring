package Alom.login.domain.token;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long refreshTokenId;

    @Column(name = "users_uuid", columnDefinition = "BINARY(16)",unique = true)
    private UUID refreshTokenUserId;

    @Column(name = "token", nullable = false)
    private String refreshTokenToken;





}
