package org.johoeunsae.exchangediary.redirect.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.config.RedirectConfig;
import org.johoeunsae.exchangediary.log.Logging;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/redirect")
@Tag(name = "리다이렉트", description = "리다이렉트 관련 API")
@Logging
public class RedirectController {

	private final RedirectConfig redirectConfig;

	@Operation(summary = "이용 가이드", description = "이용 가이드 페이지로 이동합니다.")
	@GetMapping("/guide")
	public void guide(HttpServletResponse response) throws Exception {
		response.sendRedirect(redirectConfig.getGuideUrl());
	}

	@Operation(summary = "이용약관", description = "이용약관 페이지로 이동합니다.")
	@GetMapping("/terms")
	public void terms(HttpServletResponse response) throws Exception {
		response.sendRedirect(redirectConfig.getTermsUrl());
	}


	@Operation(summary = "개인정보처리방침", description = "개인정보처리방침 페이지로 이동합니다.")
	@GetMapping("/privacy")
	public void privacy(HttpServletResponse response) throws Exception {
		response.sendRedirect(redirectConfig.getPrivacyUrl());
	}

	@Operation(summary = "오픈소스 라이센스", description = "오픈소스 라이센스 페이지로 이동합니다.")
	@GetMapping("/open-source-license")
	public void license(HttpServletResponse response) throws Exception {
		response.sendRedirect(redirectConfig.getOpenSourceLicenseUrl());
	}
}
