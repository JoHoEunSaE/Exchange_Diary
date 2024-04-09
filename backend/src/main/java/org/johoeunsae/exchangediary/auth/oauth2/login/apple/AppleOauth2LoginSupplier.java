package org.johoeunsae.exchangediary.auth.oauth2.login.apple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.AppleOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginSupplier;
import org.johoeunsae.exchangediary.auth.oauth2.login.apple.jwt.AppleJwtService;
import org.johoeunsae.exchangediary.auth.oauth2.validator.Oauth2IdentityTokenValidator;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOauth2LoginSupplier implements Oauth2LoginSupplier {
	private final AppleFeignClient appleFeignClient;
	private final AppleOauth2Config appleOauth2Config;
	private final AppleJwtService appleJwtService;

	public boolean supports(Oauth2LoginRequestVO dto) {
		log.debug("Called supports {}", dto);
		return OauthType.APPLE == dto.getOauthType();
	}
//TODO 설명 주석 추가 필요

	@Override
	public Oauth2Login supply(Oauth2LoginRequestVO dto) {
		return new AppleOauth2Login(dto, appleOauth2Config, appleJwtService, appleFeignClient);
	}

}
