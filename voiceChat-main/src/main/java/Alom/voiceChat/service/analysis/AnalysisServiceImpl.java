package Alom.voiceChat.service.analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AtomicInteger visitorCnt = new AtomicInteger(0);

    private final AtomicInteger dailyRoomCnt = new AtomicInteger(0);

    @Override
    public int getDailyVisitor() {
        return visitorCnt.get();
    }

    @Override
    public int getDailyRoomCnt() {
        return dailyRoomCnt.get();
    }

    @Override
    public int increaseVisitor() {
        return visitorCnt.incrementAndGet();
    }

    @Override
    public int decreaseVisitor() {
        return visitorCnt.decrementAndGet();
    }

    @Override
    public int increaseDailyRoomCnt() {
        return dailyRoomCnt.incrementAndGet();
    }

    @Override
    public int decreaseDailyRoomCnt() {
        return dailyRoomCnt.decrementAndGet();
    }

    @Override
    public void resetDailyInfo() {
        visitorCnt.set(0);
        dailyRoomCnt.set(0);
    }}
