package Alom.voiceChat.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${JWT.SECRET_KEY}")
    private String secretKey;

    public static String SECRET_KEY;

    @PostConstruct
    private void setData(){
        JwtUtil.SECRET_KEY = secretKey;
    }

    private static JwtUtil jwtUtil = new JwtUtil();

    public static JwtUtil getInstance(){
        return jwtUtil;
    }

    public String generateToken(String key){
        return Jwts.builder()
                .setSubject(key)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*60))
                .signWith(SignatureAlgorithm.HS256, JwtUtil.SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(JwtUtil.SECRET_KEY).parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
