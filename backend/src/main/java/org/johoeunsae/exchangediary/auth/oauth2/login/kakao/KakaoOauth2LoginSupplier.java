package org.johoeunsae.exchangediary.auth.oauth2.login.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.KakaoOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginSupplier;
import org.johoeunsae.exchangediary.auth.oauth2.validator.Oauth2IdentityTokenValidator;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOauth2LoginSupplier implements Oauth2LoginSupplier {

	private final KakaoOauth2FeignClient kakaoOauth2FeignClient;
	private final KakaoApiFeignClient kakaoApiFeignClient;
	private final KakaoOauth2Config kakaoOauth2Config;
	private final Oauth2IdentityTokenValidator oauth2IdentityTokenValidator;

	@Override
	public boolean supports(Oauth2LoginRequestVO dto) {
		log.debug("Called supports {}", dto);
		return OauthType.KAKAO == dto.getOauthType();
	}

	@Override
	public Oauth2Login supply(Oauth2LoginRequestVO dto) {
		return new KakaoOauth2Login(dto, kakaoOauth2FeignClient, kakaoApiFeignClient,
				kakaoOauth2Config, oauth2IdentityTokenValidator);
	}
}
