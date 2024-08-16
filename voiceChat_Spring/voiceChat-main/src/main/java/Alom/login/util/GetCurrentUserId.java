package Alom.login.util;

import Alom.login.auth.info.GoogleUserInfo;
import Alom.login.auth.info.KakaoUserInfo;
import Alom.login.auth.info.NaverUserInfo;
import Alom.login.auth.info.OAuth2UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GetCurrentUserId {
    public static String getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User){
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String registrationId = oAuth2User.getAttribute("registrationId");
            OAuth2UserInfo userInfo;
            if(registrationId.equals("google")){
                userInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            } else if (registrationId.equals("kakao")) {
                userInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            } else if (registrationId.equals("naver")) {
                userInfo = new NaverUserInfo(oAuth2User.getAttributes());
            }else {
                throw new IllegalArgumentException("Unsupported provider: "+registrationId);
            }
            return userInfo.getProviderId();
        }
        throw new IllegalArgumentException("Failed to get user information");
    }
}
