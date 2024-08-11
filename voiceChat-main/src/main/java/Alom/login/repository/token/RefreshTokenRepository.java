package Alom.login.repository.token;

import Alom.login.domain.token.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    @Query("SELECT u FROM RefreshToken u WHERE u.refreshTokenUserId = :refreshTokenUserId")
    RefreshToken findByUserId(UUID refreshTokenUserId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken u WHERE u.refreshTokenUserId = :refreshTokenUserId")
    void deleteByUserId(UUID refreshTokenUserId);
}
