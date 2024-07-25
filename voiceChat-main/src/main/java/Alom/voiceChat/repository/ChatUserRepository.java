package Alom.voiceChat.repository;

import Alom.voiceChat.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatUserRepository extends JpaRepository<ChatUser,Long> {
    ChatUser findByEmail(String email);
}
