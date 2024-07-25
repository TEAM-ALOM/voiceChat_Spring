package Alom.voiceChat.service.chat;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatRoomMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MsgChatService {

    private final FileService fileService;

    public ChatRoomDto chatRoomDto(String roomName, String roomPassword, boolean isPrivate, int maxUserCnt){
        ChatRoomDto room =ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .roomPassword(roomPassword)
                .isPrivate(isPrivate)
                .userCount(0)
                .maxUserCnt(maxUserCnt)
                .build();

        room.setUserList(new HashMap<String, String>());

        room.setChatType(ChatRoomDto.ChatType.MSG);

        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);

        return room;
    }

    public String addUser (Map<String,ChatRoomDto> chatRoomMap, String roomId,String userName){
        ChatRoomDto room = chatRoomMap.get(roomId);
        String userUUID= UUID.randomUUID().toString();

        HashMap<String, String> userList = (HashMap<String, String>) room.getUserList();

        userList.put(userUUID, userName);

        return userUUID;
    }

    public String isDuplicateName (Map<String,ChatRoomDto> chatRoomMap,String roomId, String username){
        ChatRoomDto room = chatRoomMap.get(roomId);
        String tmp = username;

        while (room.getUserList().containsValue(tmp)){
            int ranNum = (int) (Math.random()*100)+1;

            tmp=username+ranNum;
        }

        return tmp;

    }

    public String findUserNameByRoomIdAndUserUUID (Map<String,ChatRoomDto> chatRoomMap, String roomId,String userUUID){
        ChatRoomDto room = chatRoomMap.get(roomId);
        return (String) room.getUserList().get(userUUID);
    }

    public ArrayList<String> getUserList(Map<String ,ChatRoomDto> chatRoomMap , String roomId){
        ArrayList<String> list = new ArrayList<>();
        ChatRoomDto room = chatRoomMap.get(roomId);

        room.getUserList().forEach((key, value) -> list.add((String) value));

        return list;

    }

    public void delUser(Map<String , ChatRoomDto> chatRoomMap,String roomId,String userUUID){
        ChatRoomDto room = chatRoomMap.get(roomId);
        room.getUserList().remove(userUUID);
    }
}


