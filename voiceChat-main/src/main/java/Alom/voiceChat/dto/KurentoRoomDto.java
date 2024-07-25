package Alom.voiceChat.dto;


import Alom.voiceChat.rtc.KurentoUserSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.kurento.client.Continuation;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class KurentoRoomDto extends ChatRoomDto implements Closeable {

    private final Logger log = LoggerFactory.getLogger(KurentoRoomDto.class);
    private KurentoClient kurento;
    private MediaPipeline pipeline;

    @NotNull
    private String roomId;
    private String roomName;
    private int userCnt;
    private int maxUserCnt;

    private String roomPassword;
    private boolean isPrivate;
    private ChatType chatType;

    private ConcurrentMap<String, KurentoUserSession> participants;

    public void setRoomInfo(String roomId, String roomName, String roomPassword,boolean isPrivate, int userCnt ,
                            int maxUserCnt,ChatType chatType,KurentoClient kurento){
        this.roomId=roomId;
        this.roomName=roomName;
        this.roomPassword=roomPassword;
        this.isPrivate=isPrivate;
        this.userCnt=userCnt;
        this.maxUserCnt=maxUserCnt;
        this.chatType=chatType;
        this.kurento=kurento;
        this.participants=(ConcurrentMap<String, KurentoUserSession>) this.userList;
    }

    @Override
    public String getRoomId(){
        return roomId;
    }
    public void createPipeline(){
        this.pipeline=this.kurento.createMediaPipeline();
    }

    @PreDestroy
    private void shutdown(){this.close();}

    public Collection<KurentoUserSession> getParticipants(){
        return participants.values();
    }

    public KurentoUserSession getParticipant(String name){
        return participants.get(name);
    }

    public KurentoUserSession join(String userName, WebSocketSession session) throws IOException{
        log.info("ROOM {}: adding participant {}", this.roomId, userName);

        final KurentoUserSession participant = new KurentoUserSession(userName,this.roomId,session,this.pipeline);

        joinRoom(participant);

        participants.put(participant.getName(), participant);

        sendParticipantNames(participant);

        userCnt++;

        return participant;
    }

    public void leave(KurentoUserSession user) throws  IOException{
        log.debug("PARTICIPANT {}: Leaving room {}", user.getName(), this.roomId);

        this.removeParticipant(user.getName());

        user.close();
    }

    private Collection<String> joinRoom(KurentoUserSession newParticipant) throws IOException{

        final JsonObject newParticipantMsg = new JsonObject();

        newParticipantMsg.addProperty("id","newParticipantArrived");
        newParticipantMsg.addProperty("name", newParticipant.getName());

        final List<String> participantsList = new ArrayList<>(participants.values().size());
        log.debug("ROOM {}: 다른 참여자들에게 새로운 참여자가 들어왔음을 알림 {}", roomId,
                newParticipant.getName());
        for (final KurentoUserSession participant : participants.values()) {
            try {
                participant.sendMessage(newParticipantMsg);
            }catch (final IOException e){
                log.error("ROOM {}: participant {} could not be notified", roomId, participant.getName(), e);
            }
            participantsList.add(participant.getName());
        }
        return participantsList;
    }

    private void removeParticipant(String name) throws IOException{
        participants.remove(name);
        log.debug("ROOM {}: notifying all users that {} is leaving the room", this.roomId, name);

        final List<String> unNotifiedParticipants = new ArrayList<>();

        final JsonObject participantLeftJson = new JsonObject();

        participantLeftJson.addProperty("id","participantLeft");
        participantLeftJson.addProperty("name",name);

        for (final KurentoUserSession participant : participants.values()){
            try {
                participant.cancelVideoFrom(name);
                participant.sendMessage(participantLeftJson);
            }catch (final IOException e){
                unNotifiedParticipants.add(participant.getName());
            }
        }
        if (!unNotifiedParticipants.isEmpty()){
            log.debug("ROOM {}: The users {} could not be notified that {} left the room", this.roomId,
                    unNotifiedParticipants, name);
        }
    }

    public void sendParticipantNames (KurentoUserSession user) throws IOException {
        final JsonArray participantsArray = new JsonArray();

        for (final  KurentoUserSession participant : this.getParticipants()){
            if (!participant.equals(user)){
                final JsonElement participantName = new JsonPrimitive(participant.getName());
                participantsArray.add(participantName);
            }
        }
        final JsonObject existingParticipantMsg = new JsonObject();

        existingParticipantMsg.addProperty("id","existingParticipants");
        existingParticipantMsg.add("data",participantsArray);
        log.debug("PARTICIPANT {}: sending a list of {} participants", user.getName(),
                participantsArray.size());

        user.sendMessage(existingParticipantMsg);
    }

    @Override
    public void close() {
        for (final KurentoUserSession user : participants.values()){
            try {
                user.close();
            } catch (IOException e){
                log.debug("ROOM {}: Could not invoke close on participant {}", this.roomId, user.getName(), e);
            }
        }

        participants.clear();

        pipeline.release(new Continuation<Void>() {
            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("ROOM {}: Released Pipeline", KurentoRoomDto.this.roomId);

            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("PARTICIPANT {}: Could not release Pipeline", KurentoRoomDto.this.roomId);
            }
        });
        log.debug("Room {} closed", this.roomId);

    }
}
