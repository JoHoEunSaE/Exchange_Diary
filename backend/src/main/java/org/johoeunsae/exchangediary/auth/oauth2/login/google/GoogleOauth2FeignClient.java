package org.johoeunsae.exchangediary.auth.oauth2.login.google;

import org.johoeunsae.exchangediary.auth.oauth2.login.FeignLoggerConfiguration;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginClient;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "google-oauth2-client", url = "https://www.googleapis.com/oauth2/v3", configuration = FeignLoggerConfiguration.class)
public interface GoogleOauth2FeignClient extends Oauth2LoginClient {

	@Cacheable("GoogleOauthPublicKeyCache")
	@GetMapping("/certs")
	Oauth2PublicKeys getGooglePublicKeys();
}
