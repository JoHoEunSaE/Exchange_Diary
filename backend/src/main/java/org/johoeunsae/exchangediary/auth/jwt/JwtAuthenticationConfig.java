package org.johoeunsae.exchangediary.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationConfig {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public AuthenticationManager jwtAuthenticationManager() {
        return (Authentication authentication) -> {
            AuthenticationProvider authenticationProvider = jwtAuthenticationProvider();
            if (authenticationProvider.supports(authentication.getClass()))
                return authenticationProvider.authenticate(authentication);
            throw new AuthenticationServiceException("Unsupported authentication type: " + authentication.getClass().getName());
        };
    }

    @Bean
    public AuthenticationProvider jwtAuthenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                BearerTokenAuthenticationToken token = (BearerTokenAuthenticationToken) authentication;
                Jwt jwt = getJwt(token);
                return jwtAuthenticationConverter.convert(jwt);
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(BearerTokenAuthenticationToken.class);
            }

            private Jwt getJwt(BearerTokenAuthenticationToken token) {
                try {
                    return jwtDecoder.decode(token.getToken());
                } catch (JwtException e) {
                    throw new InvalidBearerTokenException("jwt관련 오류", e);
                } catch (RuntimeException e) {
                    throw new InvalidBearerTokenException("jwt 파싱 관련 오류", e);
                }
            }
        };
    }
}
