package Alom.voiceChat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileDto {

    private String fileName;
    private String roomId;
    private String filePath;
    private String miniDataUrl;
    private String contentType;
    private Status status;

    public enum Status{
        UPLOADED,FAIL
    }
}
