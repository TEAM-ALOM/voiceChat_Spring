package Alom.voiceChat.controller;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.service.chat.ChatServiceMain;
import Alom.voiceChat.dto.ChatRoomMap;
import Alom.voiceChat.utils.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignalHandler extends TextWebSocketHandler {

    private final RtcChatService rtcChatService;
    private final ChatServiceMain chatServiceMain;

    private final ObjectMapper objectMapper= new ObjectMapper();

    private Map<String, ChatRoomDto> rooms = ChatRoomMap.getInstance().getChatRooms();

    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER="answer";
    private static final String MSG_TYPE_ICE="ice";
    private static final String MSG_TYPE_JOIN="join";
    private static final String MSG_TYPE_LEAVE="leave";

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("[ws] Session has been closed with status [{} {}]", status, session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        sendMessage(session,new WebSocketMessage("Server",MSG_TYPE_JOIN,Boolean.toString(!rooms.isEmpty()),null,null));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage){
        try {
            WebSocketMessage message=objectMapper.readValue(textMessage.getPayload(),WebSocketMessage.class);
            log.debug("[ws] Message of {} type from {} received",message.getType(),message.getFrom());

            String userUUID = message.getFrom();
            String roomId = message.getData();

            log.info("Message {}", message.toString());

            ChatRoomDto room;

            switch (message.getType()){
                case MSG_TYPE_OFFER :
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_ICE:
                    Object candidate = message.getCandidate();
                    Object sdp = message.getSdp();
                    log.info("[ws] Signal: {}",
                            candidate != null
                                    ? candidate.toString().substring(0,64)
                                    : sdp.toString().substring(0,64));



                    ChatRoomDto roomDto =rooms.get(roomId);

                    if (roomDto != null){
                        Map<String , WebSocketSession> clients = rtcChatService.getClients(roomDto);

                        for (Map.Entry<String,WebSocketSession> client: clients.entrySet()){
                            if (!client.getKey().equals(userUUID)){
                                sendMessage(client.getValue(),
                                        new WebSocketMessage(
                                                userUUID,
                                                message.getType(),
                                                roomId,
                                                candidate,
                                                sdp
                                        ));
                            }
                        }
                    }
                    break;

                case MSG_TYPE_JOIN:
                    log.debug("[ws] {} has joined Room: #{}",userUUID,message.getData());

                    room= rtcChatService.findRoomByRoomId(roomId)
                            .orElseThrow(() -> new IOException("Invalid room number received!"));

                    room = ChatRoomMap.getInstance().getChatRooms().get(roomId);

                    rtcChatService.addClient(room, userUUID, session);

                    chatServiceMain.plusUserCnt(roomId);

                    rooms.put(roomId, room);
                    break;

                case MSG_TYPE_LEAVE:
                    log.info("[ws] {} is going to leave Room: #{}",userUUID,message.getData());

                    room = rooms.get(message.getData());

                    Optional<String> client= rtcChatService.getClients(room).keySet().stream()
                            .filter(clientListKeys  -> ObjectUtils.nullSafeEquals(clientListKeys,userUUID))
                            .findAny;

                    client.ifPresent(userID -> rtcChatService.removeClientByName(room,userID));

                    chatServiceMain.minusUserCnt(roomId);

                    log.debug("삭제 완료 [{}] ",client);

                default:
                    log.debug("[ws] Type of the received message {} is undefined!", message.getType() );
            }

        } catch (IOException e){
            log.debug("An error occurred: {} ",e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session,WebSocketMessage message){
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        }catch (IOException e){
            log.debug("An error occurred: {}",e.getMessage());
        }
    }


}
