package Alom.voiceChat.service.admin;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatRoomMap;
import Alom.voiceChat.dto.KurentoRoomDto;
import Alom.voiceChat.service.chat.KurentoManager;
import Alom.voiceChat.service.file.FileService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final KurentoManager kurentoManager;
    private final FileService fileService;
    @Override
    public Map<String, Object> getAllRooms() {

        Map<String, Object> result = new HashMap<>();
        ConcurrentMap<String , ChatRoomDto> chatRooms = ChatRoomMap.getInstance().getChatRooms();

        JsonArray joRooms = new JsonArray();
        chatRooms.values()
                .forEach(room ->{
                    JsonObject joRoom = new JsonObject();
                    joRoom.addProperty("id", room.getRoomId());
                    joRoom.addProperty("name", room.getRoomName());
                    joRoom.addProperty("password", room.getRoomPassword());
                    joRoom.addProperty("isPrivate", room.isPrivate());
                    joRoom.addProperty("type", room.getChatType().toString());
                    joRoom.addProperty("count", room.getUserCount());

                    joRooms.add(joRoom);
                });
        result.put("roomList", joRooms);
        return result;
    }

    @Override
    public String delRoom(String roomId) {
        Optional<KurentoRoomDto> kurentoRoom = Optional
                .ofNullable((KurentoRoomDto) ChatRoomMap.getInstance().getChatRooms().get(roomId));

        if (kurentoRoom.isPresent()){
            kurentoManager.removeRoom(kurentoRoom.get());
            return "success del chatroom";
        }

        fileService.deleteFileDir(roomId);

        return "no such room exist";
    }
}
