package org.johoeunsae.exchangediary.auth.oauth2.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginFactory;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2ManagerImpl implements Oauth2Manager {

	private final Oauth2LoginFactory oauth2LoginFactory;

	@Override
	public void revokeToken(Oauth2LoginRequestVO dto) {
		oauth2LoginFactory
				.create(dto)
				.ifPresent(Oauth2Login::revokeToken);
	}

	@Override
	public Optional<Oauth2LoginInfoVO> requestOauthLoginInfo(Oauth2LoginRequestVO dto) {
		return oauth2LoginFactory
				.create(dto)
				.filter(Oauth2Login::isValid)
				.map(Oauth2Login::provideLoginInfo);
	}
}
