package Alom.login.service;

import Alom.login.response.TokenResponse;

public interface TokenService {

    TokenResponse reissueAccessToken(String authorizationHeader);
}
