package org.johoeunsae.exchangediary.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RedirectConfig {

	@Value("${redirect-url.guide}")
	private String guideUrl;

	@Value("${redirect-url.terms}")
	private String termsUrl;

	@Value("${redirect-url.privacy}")
	private String privacyUrl;

	@Value("${redirect-url.open-source-license}")
	private String openSourceLicenseUrl;
}
