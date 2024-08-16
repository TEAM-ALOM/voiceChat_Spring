package Alom.socialService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendInformationDto {
    private String name;
    private String nickName;
    private String ment;
    private String iconPath;

    public FriendInformationDto(String name, String nickName, String ment, String iconPath) {
        this.name = name;
        this.nickName = nickName;
        this.ment = ment;
        this.iconPath = iconPath;
    }
}
