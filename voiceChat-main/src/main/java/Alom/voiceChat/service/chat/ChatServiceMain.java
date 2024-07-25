package Alom.voiceChat.service.chat;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatRoomMap;
import Arom.voiceChat.dto.ChatRoomDto;
import Arom.voiceChat.utils.ChatRoomMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class ChatServiceMain {

    private final MsgChatService msgChatService;
    private final RtcChatService rtcChatService;

    private final FileService fileService;

    public List<ChatRoomDto> findAllRoom() {
        List<ChatRoomDto> chatRooms = new ArrayList<>(ChatRoomMap.getInstance().getChatRooms().values());
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    public ChatRoomDto findRoomById(String roomId){
        return ChatRoomMap.getInstance().getChatRooms().get(roomId);
    }

    public ChatRoomDto createChatRoom(String roomName, String roomPassword, boolean isPrivate , int maxUserCnt, String chatType ){
        ChatRoomDto room;
        if (chatType.equals("msgChat")){
            room = msgChatService.createChatRoom(roomName, roomPassword,isPrivate,maxUserCnt);
        }
        
        else{
            room = msgChatService.createChatRoom(roomName, roomPassword,isPrivate,maxUserCnt);
        }
        return room;
    }

    public boolean confirmPassword (String roomId, String roomPassword){

        return roomPassword.equals(ChatRoomMap.getInstance().getChatRooms().get(roomId).getRoomPassword());

    }

    public void plusUserCnt (String roomId){
        log.info("cnt {}", ChatRoomMap.getInstance().getChatRooms().get(roomId).getUserCount());
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        room.setUserCount(room.getUserCount()+1);
    }

    public void minusUserCnt (String roomId){
        log.info("cnt {}", ChatRoomMap.getInstance().getChatRooms().get(roomId).getUserCount());
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        room.setUserCount(room.getUserCount()-1);
    }

    public boolean checkRoomUserCnt(String roomId){
        ChatRoomDto room =ChatRoomMap.getInstance().getChatRooms().get(roomId);
        if (room.getMaxUserCnt()+1 > room.getMaxUserCnt()){
            return false;
        }
        return true;
    }
    public void delChatRoom(String roomId){

        try {
            // 채팅방 타입에 따라서 단순히 채팅방만 삭제할지 업로드된 파일도 삭제할지 결정
            ChatRoomMap.getInstance().getChatRooms().remove(roomId);

            if (ChatRoomMap.getInstance().getChatRooms().get(roomId).getChatType().equals(ChatRoomDto.ChatType.MSG)) { // MSG 채팅방은 사진도 추가 삭제
                // 채팅방 안에 있는 파일 삭제
                fileService.deleteFileDir(roomId);
            }

            log.info("삭제 완료 roomId : {}", roomId);

        } catch (Exception e) { // 만약에 예외 발생시 확인하기 위해서 try catch
            System.out.println(e.getMessage());
        }

    }

}
