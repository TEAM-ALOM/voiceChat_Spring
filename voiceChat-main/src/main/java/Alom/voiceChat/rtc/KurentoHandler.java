package Alom.voiceChat.rtc;

import Alom.voiceChat.dto.KurentoRoomDto;
import Alom.voiceChat.service.chat.KurentoManager;
import Alom.voiceChat.service.chat.KurentoUserRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class KurentoHandler extends TextWebSocketHandler {
   private static final Logger log = LoggerFactory.getLogger(KurentoHandler.class);

    private static final Gson gson = new GsonBuilder().create();

    private final KurentoUserRegistry registry;

    private final KurentoManager roomManager;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        final JsonObject jsonMessage = gson.fromJson(message.getPayload(),JsonObject.class);

        final KurentoUserSession user = registry.getBySession(session);

        if (user!=null){
            log.debug("Incoming message from user '{}': {}", user.getName(), jsonMessage);
        } else{
            log.debug("Incoming message from new user: {}", jsonMessage);
        }

        switch (jsonMessage.get("id").getAsString()){
            case "joinRoom":
                joinRoom(jsonMessage, session);
                break;
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        KurentoUserSession user = registry.removeBySession(session);
        this.leaveRoom(user);
    }

    private void joinRoom(JsonObject params, WebSocketSession session) throws IOException{
        final String roomName = params.get("room").getAsString();
        final String name = params.get("name").getAsString();
        log.info("PARTICIPANT {}: trying to join room {}", name, roomName);

        KurentoRoomDto room = roomManager.getRoom(roomName);
        final KurentoUserSession user = room.join(name, session);

        registry.register(user);
    }

    private void leaveRoom(KurentoUserSession user) throws IOException{
        if (Objects.isNull(user)){
            return;
        }
        final KurentoRoomDto room=roomManager.getRoom(user.getRoomName());

        if (!room.getParticipants().contains(user)){
            return;
        }

        room.leave(user);

        room.setUserCount(room.getUserCount()-1);
    }

    private void connectException(KurentoUserSession user,Exception e) throws IOException{
        JsonObject message= new JsonObject();
        message.addProperty("id","ConnectionFail");
        message.addProperty("data", e.getMessage());

        user.sendMessage(message);
    }

}
