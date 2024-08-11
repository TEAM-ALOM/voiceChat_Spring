package Alom.login.auth.handler;

import Alom.login.auth.info.GoogleUserInfo;
import Alom.login.auth.info.KakaoUserInfo;
import Alom.login.auth.info.NaverUserInfo;
import Alom.login.auth.info.OAuth2UserInfo;
import Alom.login.domain.token.RefreshToken;
import Alom.login.domain.user.User;
import Alom.login.repository.token.RefreshTokenRepository;
import Alom.login.repository.user.UserRepository;
import Alom.login.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${spring.jwt.redirect}")
    private String REDIRECT_URI;

    @Value("${spring.jwt.access_token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${spring.jwt.refresh-token.expiration-time")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private OAuth2UserInfo oAuth2UserInfo = null;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        final String provider = token.getAuthorizedClientRegistrationId();

        switch (provider){
            case "google"->{
                log.info("구글 로그인 요청");
                oAuth2UserInfo = new GoogleUserInfo(token.getPrincipal().getAttributes());
            }
            case "kakao"-> {
                log.info("카카오 로그인 요청");
                oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());
            }
            case "naver"->{
                log.info("네이버 로그인 요청");
                oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) token.getPrincipal().getAttributes().get("response"));
            }

        }
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        User existUser = userRepository.findByProviderId(providerId);
        User user;

        if (existUser == null){
            log.info("신규 유저입니다. 등록을 진행합니다.");
            user = User.builder()
                    .userUuid(UUID.randomUUID())
                    .userName(name)
                    .userProvider(provider)
                    .userProviderId(providerId)
                    .build();
        }
        else{
            log.info("기존 유저입니다.");
            refreshTokenRepository.deleteByUserId(existUser.getUserUuid());
            user = existUser;
        }
        log.info("유저 이름 : {}",name);
        log.info("PROVIDER : {}",provider);
        log.info("PROVIDER_ID : {}",providerId);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserUuid(),REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .refreshTokenUserId(user.getUserUuid())
                .refreshTokenToken(refreshToken)
                .build();
        refreshTokenRepository.save(newRefreshToken);
        String accessToken = jwtUtil.generateAccessToken(user.getUserUuid(),ACCESS_TOKEN_EXPIRATION_TIME);
        String encodedName = URLEncoder.encode(name,"UTF-8");
        String redirectUri = String.format(REDIRECT_URI,encodedName,accessToken,refreshToken);
        getRedirectStrategy().sendRedirect(request,response,redirectUri);
    }
}
