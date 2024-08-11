package Alom.voiceChat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyInfoDto {

    private Long id;
    private int visitorCount;
    private int dailyRoomCreate;
    private ChatRoomDto.ChatType;
    private LocalDate date;
}
