package org.johoeunsae.exchangediary.auth.oauth2.login.kakao;

import org.johoeunsae.exchangediary.auth.oauth2.login.FeignLoggerConfiguration;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginClient;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "kakao-public-key-client", url = "https://kauth.kakao.com/.well-known/jwks.json", configuration = FeignLoggerConfiguration.class)
public interface KakaoOauth2FeignClient extends Oauth2LoginClient {

	@Cacheable("KakaoOauthPublicKeyCache")
	@GetMapping("/")
	Oauth2PublicKeys getKakaoPublicKeys();
}
