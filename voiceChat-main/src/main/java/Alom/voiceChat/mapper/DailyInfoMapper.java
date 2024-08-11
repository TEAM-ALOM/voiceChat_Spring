package Alom.voiceChat.mapper;

import Alom.voiceChat.dto.DailyInfoDto;
import Alom.voiceChat.entity.DailyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DailyInfoMapper {
    DailyInfo INSTANCE = Mappers.getMapper(DailyInfo.class);

    DailyInfoDto toDto(DailyInfo e);

    DailyInfo toEntity(DailyInfoDto d);
}
