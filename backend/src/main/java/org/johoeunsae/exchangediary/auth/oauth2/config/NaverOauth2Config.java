package org.johoeunsae.exchangediary.auth.oauth2.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
public class NaverOauth2Config {

	@Value("${oauth.naver.client-id}")
	private String clientId;
	@Value("${oauth.naver.client-secret}")
	private String clientSecret;
}
