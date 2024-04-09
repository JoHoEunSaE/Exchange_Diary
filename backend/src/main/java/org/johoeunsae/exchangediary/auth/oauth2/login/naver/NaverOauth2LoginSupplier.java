package org.johoeunsae.exchangediary.auth.oauth2.login.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.GoogleOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.config.NaverOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginSupplier;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverOauth2LoginSupplier implements Oauth2LoginSupplier {
	private final NaverFeignClient naverFeignClient;
	private final NaverNidFeignClient naverNidFeignClient;
	private final NaverOauth2Config naverOauth2Config;


	@Override
	public boolean supports(Oauth2LoginRequestVO dto) {
		log.debug("Called supports {}", dto);
		return OauthType.NAVER == dto.getOauthType();
	}

	@Override
	public Oauth2Login supply(Oauth2LoginRequestVO dto) {
		return new NaverOauth2Login(dto, naverFeignClient, naverOauth2Config, naverNidFeignClient);
	}
}
