package Alom.voiceChat.rtc;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class KurentoUserSession implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(KurentoUserSession.class);
    private final String name;

    private final WebSocketSession session;

    private final MediaPipeline pipeline;

    private final String roomName;

    private final WebRtcEndpoint outgoingMedia;

    private final ConcurrentMap<String , WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

    public KurentoUserSession(String name, String roomName,
                              WebSocketSession session, MediaPipeline pipeline){
        this.pipeline = pipeline;
        this.name = name;
        this.session = session;
        this.roomName = roomName;

        this.outgoingMedia = new WebRtcEndpoint.Builder(pipeline)
                .useDataChannels()
                .build();

        this.outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();

                response.addProperty("id","iceCandidate");

                response.addProperty("name",name);

                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));

                try{
                    synchronized (session){
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e){
                        log.debug(e.getMessage());
                }
            }
        });
    }

    public WebRtcEndpoint getOutgoingWebRtcPeer() {
        return outgoingMedia;
    }

    public ConcurrentMap<String, WebRtcEndpoint> getIncomingMedia() {
        return incomingMedia;
    }

    public String getName(){
        return name;
    }

    public WebSocketSession getSession(){
        return session;
    }

    public String getRoomName(){
        return this.roomName;
    }

    public void receiveVideoFrom(KurentoUserSession sender, String sdpOffer) throws IOException{
        log.info("USER {}: connecting with {} in room {}", this.name, sender.getName(), this.roomName);

        log.trace("USER {}: SdpOffer for {} is {}",this.name,sender.getName(),sdpOffer);


        final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);

        final JsonObject scParams = new JsonObject();

        scParams.addProperty("id","receiveVideoAnswer");
        scParams.addProperty("name", sender.getName());
        scParams.addProperty("sdpAnswer", ipSdpAnswer);

        log.trace("USER {}: SdpAnswer for {} is {}", this.name, sender.getName(), ipSdpAnswer);
        this.sendMessage(scParams);
        log.debug("gather candidates");
        this.getEndpointForUser(sender).gatherCandidates();

    }

    private WebRtcEndpoint getEndpointForUser(final KurentoUserSession sender){
        if (sender.getName().equals(name)){
            log.debug("PARTICIPANT {}: configuring loopback",this.name);
           return outgoingMedia;
        }

        log.debug("PARTICIPANT {}: receiving video from {}", this.name, sender.getName());

        WebRtcEndpoint incomingMedia = this.incomingMedia.get(sender.getName());

        if (incomingMedia == null){
            log.debug("PARTICIPANT {}: creating new endpoint for {}",this.name,sender.getName());

            incomingMedia = new WebRtcEndpoint.Builder(pipeline)
                    .useDataChannels()
                    .build();

            incomingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
                @Override
                public void onEvent(IceCandidateFoundEvent event) {
                    JsonObject response= new JsonObject();

                    response.addProperty("id","iceCandidate");
                    response.addProperty("name", sender.getName());

                    response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));

                    try{
                        synchronized (session){
                            session.sendMessage(new TextMessage(response.toString()));
                        }
                    }catch (IOException e){
                        log.debug(e.getMessage());
                    }
                }
            });
            this.incomingMedia.put(sender.getName(), incomingMedia);
        }

        log.debug("PARTICIPANT {}: obtained endpoint for {}", this.name, sender.getName());

        sender.getOutgoingWebRtcPeer().connect(incomingMedia);

        return incomingMedia;
    }

    public void cancelVideoFrom(final KurentoUserSession sender){
        this.cancelVideoFrom(sender.getName());
    }

    public void cancelVideoFrom(final String senderName){
        log.debug("PARTICIPANT {}: canceling video reception from {}", this.name, senderName);
        final WebRtcEndpoint incoming = incomingMedia.remove(senderName);
        log.debug("PARTICIPANT {}: removing endpoint for {}", this.name, senderName);

        if (Objects.nonNull(incoming)){
            incoming.release(new Continuation<Void>() {
                @Override
                public void onSuccess(Void result) throws Exception {
                    log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                            KurentoUserSession.this.name, senderName);

                }

                @Override
                public void onError(Throwable cause) throws Exception {
                    log.warn("PARTICIPANT {}: Could not release incoming EP for {}", KurentoUserSession.this.name, senderName);

                }
            });
        }
    }
    public void sendMessage(JsonObject message) throws IOException{
        log.debug("USER {}: Sending message {}",name,message);
        synchronized (session){
            try {
                session.sendMessage((new TextMessage(message.toString())));
            }catch (Exception e){
                message.addProperty("id","ConnectionFail");
                message.addProperty("data", e.getMessage());
                this.sendMessage(message);
            }
        }
    }

    @Override
    public void close() throws IOException {
        log.debug("PARTICIPANT {}: Releasing resources", this.name);
        for(final String remoteParticipantName : incomingMedia.keySet()){
            log.trace("PARTICIPANT {}: Released incoming EP for {}", this.name, remoteParticipantName);

            final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);

            ep.release(new Continuation<Void>() {
                @Override
                public void onSuccess(Void result) throws Exception {
                    log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                            KurentoUserSession.this.name, remoteParticipantName);
                }

                @Override
                public void onError(Throwable cause) throws Exception {
                    log.warn("PARTICIPANT {}: Could not release incoming EP for {}", KurentoUserSession.this.name,
                            remoteParticipantName);
                }
            });
        }
    }
    public void addCandidate(IceCandidate candidate,String name){
        if (this.name.compareTo(name)==0){
            outgoingMedia.addIceCandidate(candidate);
        }else{
            WebRtcEndpoint webRtc = incomingMedia.get(name);
            if (webRtc!=null){
                webRtc.addIceCandidate(candidate);
            }
        }
    }
    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (obj==null || !(obj instanceof KurentoUserSession)){
            return false;
        }
        KurentoUserSession other = (KurentoUserSession) obj;

        boolean eq = name.equals(other.name);

        eq &= roomName.equals(other.roomName);
        return eq;
    }

    @Override
    public int hashCode(){
        int result=1;

        result = 31 * result * name.hashCode();
        result = 31 * result * roomName.hashCode();

        return result;
    }
}
