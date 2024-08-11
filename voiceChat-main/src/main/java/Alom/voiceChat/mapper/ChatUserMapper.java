package Alom.voiceChat.mapper;

import Alom.voiceChat.dto.ChatUserDto;
import Alom.voiceChat.entity.ChatUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChatUserMapper {
    ChatUserMapper INSTANCE = Mappers.getMapper(ChatUserMapper.class);

    ChatUserDto toDto(ChatUser e);

    ChatUser toEntity(ChatUserDto d);

}
