package Alom.voiceChat.service.monitoring;

import Alom.voiceChat.dto.ClientInfo;

public interface ClientCheckService {
    Boolean checkBlackList(ClientInfo clientInfo);

    Boolean checkIsAllowedIp(String cidr);
}
