package Alom.login.service.impl;

import Alom.login.domain.token.RefreshToken;
import Alom.login.domain.token.exception.TokenErrorResult;
import Alom.login.domain.token.exception.TokenException;
import Alom.login.repository.token.RefreshTokenRepository;
import Alom.login.response.TokenResponse;
import Alom.login.service.TokenService;
import Alom.login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;
    private  final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public TokenResponse reissueAccessToken(String authorizationHeader){
        String refreshToken = jwtUtil.getTokenFromHeader(authorizationHeader);
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findByUserId(UUID.fromString(userId));
        String accessToken = null;
        if (!existRefreshToken.getRefreshTokenToken().equals(refreshToken)||jwtUtil.isTokenExpired(refreshToken)){
            throw new TokenException(TokenErrorResult.INVALID_REFRESH_TOKEN);
        }else {
            accessToken = jwtUtil.generateAccessToken(UUID.fromString(userId),ACCESS_TOKEN_EXPIRATION_TIME);
        }
        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

}
