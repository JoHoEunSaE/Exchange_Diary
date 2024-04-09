package org.johoeunsae.exchangediary.auth.oauth2.login.apple;

import feign.Headers;
import org.johoeunsae.exchangediary.auth.oauth2.login.FeignLoggerConfiguration;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginClient;
import org.johoeunsae.exchangediary.auth.oauth2.login.apple.dto.AppleTokenResponse;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import feign.FeignException.FeignClientException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "apple-public-key-client", url = "https://appleid.apple.com/auth", configuration = FeignLoggerConfiguration.class)
public interface AppleFeignClient extends Oauth2LoginClient {

	@Cacheable("AppleOauthPublicKeyCache")
	@GetMapping("/keys")
	Oauth2PublicKeys getApplePublicKeys() throws FeignClientException;

	@PostMapping("/token")
	AppleTokenResponse generateToken(
			@RequestParam String client_id,
			@RequestParam String client_secret,
			@RequestParam String code,
			@RequestParam String grant_type
	) throws FeignClientException;

	@PostMapping("/revoke")
	void revokeToken(
			@RequestHeader("Content-Type") String contentType,
			@RequestParam String client_id,
			@RequestParam String client_secret,
			@RequestParam String token
	) throws FeignClientException;
}
