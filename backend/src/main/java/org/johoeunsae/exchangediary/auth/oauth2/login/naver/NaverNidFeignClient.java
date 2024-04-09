package org.johoeunsae.exchangediary.auth.oauth2.login.naver;

import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginClient;
import org.johoeunsae.exchangediary.auth.oauth2.vo.AccessTokenValidationVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.NaverLoginInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-nid-client", url = "https://nid.naver.com/")
public interface NaverNidFeignClient extends Oauth2LoginClient {

	@PostMapping("/oauth2.0/token")
	void revokeToken(
			@RequestParam String grant_type,
			@RequestParam String client_id,
			@RequestParam String client_secret,
			@RequestParam String access_token,
			@RequestParam String service_provider
	);

}
