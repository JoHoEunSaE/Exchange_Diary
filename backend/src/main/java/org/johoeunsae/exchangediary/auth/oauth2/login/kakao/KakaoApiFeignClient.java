package org.johoeunsae.exchangediary.auth.oauth2.login.kakao;

import org.johoeunsae.exchangediary.auth.oauth2.login.FeignLoggerConfiguration;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginClient;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

//https://kapi.kakao.com/v1/user/unlink
@FeignClient(name = "kakao-api-client", url = "https://kapi.kakao.com/v1", configuration = FeignLoggerConfiguration.class)
public interface KakaoApiFeignClient extends Oauth2LoginClient {
	@PostMapping("/user/unlink")
	void revokeToken(@RequestHeader("Authorization") String accessToken);
}
