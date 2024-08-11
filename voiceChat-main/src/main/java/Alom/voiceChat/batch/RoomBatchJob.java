package Alom.voiceChat.batch;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatRoomMap;
import Alom.voiceChat.repository.DailyInfoRepository;
import Alom.voiceChat.service.analysis.AnalysisService;
import Alom.voiceChat.service.chat.KurentoManager;
import Alom.voiceChat.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomBatchJob {
    private final KurentoManager kurentoManager;
    private final AnalysisService analysisService;
    private final DailyInfoRepository dailyInfoRepository;
    private final FileService fileService;

    @Scheduled(cron = "0 3,30 * * * *",zone = "Asia/Seoul")
    public void checkRoom(){
        ConcurrentMap<String, ChatRoomDto> chatRooms = ChatRoomMap.getInstance().getChatRooms();


        AtomicInteger delRoomCnt = new AtomicInteger();

        chatRooms.keySet()
                .forEach(key ->{
                    ChatRoomDto room = chatRooms.get(key);

                    if (room.getUserCount())

                });
    }

}
