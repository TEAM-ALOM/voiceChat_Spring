package Alom.voiceChat.service.monitoring;

import Alom.voiceChat.controller.ExceptionController;
import Alom.voiceChat.dto.ClientInfo;
import jakarta.annotation.PostConstruct;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
public class ClientCheckServiceImpl implements ClientCheckService {

    private static final Logger log = LoggerFactory.getLogger(ClientCheckService.class);

    private final String blackListJsonPath ="geodata/firehol_level1.txt";


    @Value("${endpoint.allowed_subnet}")
    private List<String> allowedSubnet;

    @Value("${endpoint.allowed_ip_addresses}")
    private List<String> allowedIPAddresses;

    @PostConstruct
    private void initBlackListJson(){
        this.blackListJson(blackListJsonPath);
    }

    @Override
    public Boolean checkBlackList(ClientInfo clientInfo) {
        List<String> blackList = blackListJson(blackListJsonPath);

        log.debug("##########################################");
        log.debug("clientInfo :::: " + clientInfo.toString());
        log.debug("##########################################");

        log.debug("##########################################");
        log.debug("blackList ::: " + blackList.toString());
        log.debug("##########################################");

        boolean isBlack = blackList.stream().anyMatch(black ->{
            return clientInfo.getSubnet().equals(black);
        });

        if (isBlack){
            clientInfo.setBlack(true);
        }
        return isBlack;
    }

    @Override
    public Boolean checkIsAllowedIp(String cidr) {
        return null;
    }

    private boolean isInRange(String cidr, String ip) throws UnknownHostException{
        String[] parts = cidr.split("/");
        String ipSection = parts[0];

        int prefix = (parts.length < 2) ? 0 : Integer.parseInt(parts[1]);

        InetAddress ipAddr = InetAddress.getByName(ip);

        BitSet ipBits = BitSet.valueOf(ipAddr.getAddress());

        InetAddress networkAddr = InetAddress.getByName(ipSection);
        BitSet networkBits = BitSet.valueOf(networkAddr.getAddress());

        int maxLength = Math.max(ipBits.length(), networkBits.length());

        if (maxLength<prefix){
            maxLength = prefix;
        }

        ipBits.clear(prefix, maxLength);
        networkBits.clear(prefix,maxLength);

        return ipBits.equals(networkBits);

    }

    @Cacheable("blackList")
    public List<String> blackListJson(String path) {
        try {
            ClassPathResource blackList = new ClassPathResource(path);

            log.debug("blackList URI :: " + blackList.getURI());

            try (InputStream inputStream = blackList.getInputStream()){
                return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.toList());
            }
        }catch (Exception e){
            log.error("error path :: " + path);
            throw new ExceptionController.ResourceNotFoundException("there is No BlackList file");
        }
    }
}
