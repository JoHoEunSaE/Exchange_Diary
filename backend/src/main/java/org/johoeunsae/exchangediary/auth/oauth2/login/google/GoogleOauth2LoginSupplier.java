package org.johoeunsae.exchangediary.auth.oauth2.login.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.GoogleOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginSupplier;
import org.johoeunsae.exchangediary.auth.oauth2.validator.Oauth2IdentityTokenValidator;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOauth2LoginSupplier implements Oauth2LoginSupplier {

    private final GoogleOauth2FeignClient googleOauth2FeignClient;
    private final GoogleFeignClient googleFeignClient;
    private final GoogleOauth2Config googleOauth2Config;
    private final Oauth2IdentityTokenValidator oauth2IdentityTokenValidator;


    @Override
    public boolean supports(Oauth2LoginRequestVO dto) {
        log.debug("Called supports {}", dto);
        return OauthType.GOOGLE == dto.getOauthType();
    }

    @Override
    public Oauth2Login supply(Oauth2LoginRequestVO dto) {
        return new GoogleOauth2Login(dto, googleOauth2FeignClient, googleFeignClient,
                googleOauth2Config, oauth2IdentityTokenValidator);
    }
}
