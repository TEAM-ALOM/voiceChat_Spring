package Alom.voiceChat.service.chat;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatRoomMap;
import Alom.voiceChat.utils.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RtcChatService {

    public ChatRoomDto createChatRoom(String roomName, String roomPassword , boolean isPrivate, int maxUserCnt){
        ChatRoomDto room = ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .roomPassword(roomPassword)
                .isPrivate(isPrivate)
                .userCount(0)
                .maxUserCnt(maxUserCnt)
                .build();

        room.setUserList(new HashMap<String, WebSocketSession>());

        room.setChatType(ChatRoomDto.ChatType.RTC);

        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);
        return room;
    }
    public Map<String,WebSocketSession> getClients(ChatRoomDto room){
        Optional <ChatRoomDto> roomDto = Optional.ofNullable(room);

        return (Map<String, WebSocketSession>) roomDto.get().getUserList();
    }

    public Map<String,WebSocketSession> addClient(ChatRoomDto room,String name, WebSocketSession session){
        Map<String, WebSocketSession> userList = (Map<String, WebSocketSession>) room.getUserList();
        userList.put(name, session);
        return userList;
    }
    public void removeClientByName(ChatRoomDto room, String userUUID){
        room.getUserList().remove(userUUID);
    }

    public boolean findUserCount (WebSocketMessage webSocketMessage){
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(webSocketMessage.getData());

        return room.getUserList().size() > 1;
    }

}
