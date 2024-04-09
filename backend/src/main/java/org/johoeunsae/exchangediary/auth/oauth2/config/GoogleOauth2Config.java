package org.johoeunsae.exchangediary.auth.oauth2.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
public class GoogleOauth2Config {

	@Value("${oauth.google.iss}")
	private String iss;

	@Value("${oauth.google.client-id}")
	private String clientId;

//	TODO: 추후 nonce 값을 사용하도록 수정
//	@Value("${oauth.google.nonce}")
//	private String nonce;
}
