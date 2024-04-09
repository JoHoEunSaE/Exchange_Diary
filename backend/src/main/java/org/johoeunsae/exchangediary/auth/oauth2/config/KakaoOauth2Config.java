package org.johoeunsae.exchangediary.auth.oauth2.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
public class KakaoOauth2Config {

	@Value("${spring.config.activate.on-profile}")
	private String activeProfile;

	@Value("${oauth.kakao.iss}")
	private String iss;

	@Value("${oauth.kakao.client-id}")
	private String clientId;

	@Value("${oauth.kakao.native-app-key}")
	private String nativeAppKey;

	public String getClientId() {
		if (activeProfile.equals("local")) {
			return clientId;
		}
		return nativeAppKey;
	}

//	TODO: 추후 nonce 값을 사용하도록 수정
//	@Value("${oauth.kakao.nonce}")
//	private String nonce;
}
