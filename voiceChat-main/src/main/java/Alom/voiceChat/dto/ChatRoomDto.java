package Alom.voiceChat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    @NotNull
    private String roomId;
    private String roomName;
    private int userCount;
    private int maxUserCnt;

    private String roomPassword;
    private boolean isPrivate;

    private ChatType chatType;

    public ConcurrentMap<String ,?> userList= new ConcurrentHashMap<>();
}
