package Alom.voiceChat.service.chat;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatRoomMap;
import Alom.voiceChat.dto.KurentoRoomDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class KurentoManager {

    private final Logger log = LoggerFactory.getLogger(KurentoManager.class);

    private final ConcurrentMap<String, ChatRoomDto> rooms = ChatRoomMap.getInstance().getChatRooms();

    public KurentoRoomDto getRoom(String roomId){
        log.debug("Searching for room {}",roomId);


        KurentoRoomDto room = (KurentoRoomDto) rooms.get(roomId);

        return room;
    }

    public void removeRoom(KurentoRoomDto room){
        this.rooms.remove(room.getRoomId());

        room.close();

        log.info("Room {} removed and closed", room.getRoomId());

    }
}
