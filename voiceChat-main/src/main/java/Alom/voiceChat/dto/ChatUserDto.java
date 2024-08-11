package Alom.voiceChat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDto {
    private Long id;
    private String nickName;
    private String password;
    private String email;
    private String provider;
}
