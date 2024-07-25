package Alom.voiceChat.repository;

import Alom.voiceChat.entity.DailyInfo;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyInfoRepository extends JpaRepository<DailyInfo, Long> {
    DailyInfoRepository findByDate(LocalDate date);
}
