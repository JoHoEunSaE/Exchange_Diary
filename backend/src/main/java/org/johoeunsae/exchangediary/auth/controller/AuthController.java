package org.johoeunsae.exchangediary.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.jwt.JwtLoginTokenDto;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterRequestDTO;
import org.johoeunsae.exchangediary.auth.oauth2.service.Oauth2Service;
import org.johoeunsae.exchangediary.auth.oauth2.vo.LoginResultVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Logging
public class AuthController {

	private final Oauth2Service oauth2Service;

	@Operation(summary = "로그인", description = "소셜기관에서 발급받은 토큰(ID 토큰 or 인증 토큰)을 바탕으로 로그인을 수행하는 API입니다. 유저의 디바이스 토큰 정보도 요구됩니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그인 성공",
					content = @Content(schema = @Schema(implementation = JwtLoginTokenDto.class))
			),
	})
	@ApiErrorCodeExample(
			authExceptionStatuses = {
					AuthExceptionStatus.OAUTH_BAD_GATEWAY,
					AuthExceptionStatus.ALREADY_EXIST_MEMBER,
			}
	)
	@PostMapping("/login")
	public ResponseEntity<JwtLoginTokenDto> login(@RequestBody Oauth2LoginRequestVO dto) {
		LocalDateTime now = LocalDateTime.now();
		LoginResultVO loginResult = oauth2Service.login(dto, now);
		if (loginResult.isNew()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(loginResult.getJwtLoginToken());
		} else {
			return ResponseEntity.ok(loginResult.getJwtLoginToken());
		}
	}

	@Operation(summary = "회원탈퇴", description = "회원탈퇴를 수행(소셜기관에 발급받은 토근 폐기)하는 API입니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그아웃 성공"),
	})
	@ApiErrorCodeExample(
			authExceptionStatuses = {
					AuthExceptionStatus.UNAUTHORIZED_MEMBER,
					AuthExceptionStatus.IDENTITY_TOKEN_INVALID,
					AuthExceptionStatus.NOT_FOUND_MEMBER,
					AuthExceptionStatus.NOT_ACTIVE_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/unregister")
	public void unregister(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestBody UnregisterRequestDTO dto) {
		oauth2Service.unregister(userSessionDto, dto);
	}
}
