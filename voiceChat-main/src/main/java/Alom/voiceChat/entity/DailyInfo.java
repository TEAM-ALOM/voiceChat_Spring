package Alom.voiceChat.entity;

import Alom.voiceChat.dto.ChatRoomDto;
import Alom.voiceChat.dto.ChatType;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Table
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int dailyVisitor;

    @Column
    private int dailyRoomCnt;

    @Column
    @Enumerated(EnumType.STRING)
    private ChatType mostFavoriteType;

    @Column
    private LocalDate date;


}
