package Alom.voiceChat.service.chat;


import Alom.voiceChat.rtc.KurentoUserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map of users registered in the system. This class has a concurrent hash map to store users, using
 * its name as key in the map.
 *
 * 유저를 관리하는 클래스로 concurrent hash map 을 쓰는데 유저명을 key 로 사용함
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @author Ivan Gracia (izanmail@gmail.com)
 * @modifyBy SeJon Jang (wkdtpwhs@gmail.com)
 */

@Service
@RequiredArgsConstructor
public class KurentoUserRegistry {

    /**
     * @Desc 유저명 - userSession 객체 저장 map
     */

    private final ConcurrentHashMap<String, KurentoUserSession> userByName = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, KurentoUserSession> usersBySessionId = new ConcurrentHashMap<>();

    public void register(KurentoUserSession user){
        userByName.put(user.getName(), user);
        usersBySessionId.put(user.getSession().getId(), user);
    }


    public KurentoUserSession getByName(String name){
        return userByName.get(name);
    }

    public KurentoUserSession getBySession(WebSocketSession session){
        return usersBySessionId.get(session.getId());
    }

    public boolean exists(String name){
        return userByName.keySet().contains(name);
    }

    public KurentoUserSession removeBySession(WebSocketSession session){
        final KurentoUserSession user=getBySession(session);
        if (Objects.nonNull(user)){
            userByName.remove(user.getName());
            usersBySessionId.remove(session.getId());
        }
        return user;
    }
}
