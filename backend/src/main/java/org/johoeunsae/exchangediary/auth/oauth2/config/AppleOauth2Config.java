package org.johoeunsae.exchangediary.auth.oauth2.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
@ToString
public class AppleOauth2Config {

	public static final String KEY_ID = "kid";
	public static final String ALGORITHM = "alg";

	@Value("${oauth.apple.iss}")
	private String iss;

	@Value("${oauth.apple.bundle-id}")
	private String bundleId;

	@Value("${oauth.apple.service-id}")
	private String serviceId;

	@Value("${oauth.apple.key-id}")
	private String keyId;

	@Value("${oauth.apple.team-id}")
	private String teamId;

	@Value("${oauth.apple.key-path}")
	private String keyPath;

	@Value("${oauth.apple.id-url}")
	private String appleIdUrl;

	@Value("${oauth.apple.jwt.alg}")
	private String alg;

//	TODO: 추후 nonce 값을 사용하도록 수정
//	@Value("${oauth.apple.nonce}")
//	private String nonce;
}
