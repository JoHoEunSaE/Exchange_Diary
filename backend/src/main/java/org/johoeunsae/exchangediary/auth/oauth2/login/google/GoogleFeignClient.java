package org.johoeunsae.exchangediary.auth.oauth2.login.google;

import feign.FeignException.FeignClientException;
import org.johoeunsae.exchangediary.auth.oauth2.login.FeignLoggerConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "google-client", url = "https://oauth2.googleapis.com/", configuration = FeignLoggerConfiguration.class)
public interface GoogleFeignClient {

	@PostMapping("/revoke")
	void tokenRevoke(
			@RequestParam String token,
			@RequestBody String body
	) throws FeignClientException;
}
