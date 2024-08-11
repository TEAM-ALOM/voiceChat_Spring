package Alom.login.auth.info;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
}
