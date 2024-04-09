package org.johoeunsae.exchangediary.auth.oauth2.login.apple;

import feign.FeignException.FeignClientException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.AppleOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.apple.dto.AppleTokenResponse;
import org.johoeunsae.exchangediary.auth.oauth2.login.apple.jwt.AppleJwtService;
import org.johoeunsae.exchangediary.auth.oauth2.utils.IdentityTokenParser;
import org.johoeunsae.exchangediary.auth.oauth2.validator.Oauth2IdentityTokenValidator;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;

//TODO 설명 주석 추가 필요
@RequiredArgsConstructor
@Slf4j
public class AppleOauth2Login implements Oauth2Login {

    private final Oauth2LoginRequestVO dto;
    private final AppleOauth2Config appleOauth2Config;
    private final AppleJwtService appleJwtService;
    private final AppleFeignClient appleFeignClient;

    /**
     * Apple Login 의 경우, isValid
     *
     * @return
     */
    @Override
    public boolean isValid() {
        log.debug("Called isValid {}", dto);
        return true;
    }

    private AppleTokenResponse getAccessToken(String code, String clientSecret) {
        log.debug("Called getAccessToken {}", dto);
        final String AUTHORIZATION_CODE = "authorization_code";

        try {
            AppleTokenResponse appleTokenResponse = appleFeignClient.generateToken(
                    appleOauth2Config.getBundleId(),
                    clientSecret,
                    code,
                    AUTHORIZATION_CODE);
            return appleTokenResponse;
        } catch (FeignClientException e) {
            log.error("{}", e.getMessage());
            throw new ServiceException(AuthExceptionStatus.IDENTITY_TOKEN_INVALID);
        }
    }

    @Override
    public Oauth2LoginInfoVO provideLoginInfo() {
        log.debug("Called provideLoginInfo {}", dto);

        final String SUB = "sub";
        final String EMAIL = "email";

        String clientSecret = appleJwtService.createClientSecret();
        AppleTokenResponse appleTokenResponse = getAccessToken(dto.getValid(), clientSecret);

        Map<String, String> parsedClaims = IdentityTokenParser.parseClaims(appleTokenResponse.getIdToken());

        return Oauth2LoginInfoVO.builder()
                .oauthId(parsedClaims.get(SUB))
                .email(parsedClaims.get(EMAIL))
                .deviceToken(dto.getDeviceToken())
                .oauthType(dto.getOauthType())
                .refreshToken(Optional.of(appleTokenResponse.getRefreshToken()))
                .build();
    }


    @Override
    public void revokeToken() {
        log.debug("Called revokeToken {}", dto);
        try {
            final String CONTENT_TYPE = "application/x-www-form-urlencoded";

            String clientSecret = appleJwtService.createClientSecret();

            appleFeignClient.revokeToken(CONTENT_TYPE,
                    appleOauth2Config.getBundleId(), clientSecret, dto.getValid());
        } catch (FeignClientException e) {
            log.error("{}", e.getMessage());
            throw new ServiceException(AuthExceptionStatus.IDENTITY_TOKEN_INVALID);
        }
    }
}
