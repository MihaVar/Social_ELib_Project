package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Token;
import org.mvar.social_elib_project.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token createToken(String token, Date expirationDate) {
        if (!tokenRepository.existsByJwt(token)) {
            return tokenRepository.save(
                    Token.builder()
                            .jwt(token)
                            .isValid(true)
                            .expireDate(expirationDate)
                            .build()
            );
        }
        throw new NullPointerException();
    }

    public void removeInvalidTokens() {
        tokenRepository.deleteTokenByExpireDate(new Date());
    }

    public void invalidateToken(String jwtToken) {
        Token storedToken = getTokenInformation(jwtToken);
        storedToken.setValid(false);
        tokenRepository.save(storedToken);
    }

    public Token getTokenInformation(String jwtToken) {
        return tokenRepository.findByJwt(jwtToken).orElse(null);
    }
}
