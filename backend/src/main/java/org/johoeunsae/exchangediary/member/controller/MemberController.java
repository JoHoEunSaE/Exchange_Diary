package org.johoeunsae.exchangediary.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.MemberUpdateDto;
import org.johoeunsae.exchangediary.dto.MemberUpdateRequestDto;
import org.johoeunsae.exchangediary.dto.ProfileDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.CloudExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.UtilsExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.member.service.MemberQueryService;
import org.johoeunsae.exchangediary.member.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "멤버", description = "멤버 API")
@Logging
public class MemberController {

	private static final String OAUTH_TYPE = "oauthType";

	private final MemberService memberService;
	private final MemberQueryService memberQueryService;

	@GetMapping("/me/profile")
	@Operation(summary = "본인 프로필 조회", description = "자신의 프로필을 조회합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "ok"))
	@ApiErrorCodeExample(authExceptionStatuses = {AuthExceptionStatus.UNAUTHORIZED_MEMBER})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public ProfileDto getMyProfile(
			@LoginUserInfo UserSessionDto userSessionDto) {
		return memberQueryService.getMemberProfile(userSessionDto.getUserId(),
				userSessionDto.getUserId());
	}

	@PatchMapping(value = "/me/profile")
	@Operation(summary = "본인 프로필 수정", description = "자신의 프로필을 수정합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "ok"))
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHORIZED_MEMBER},
			utilsExceptionStatuses = {UtilsExceptionStatus.INVALID_FILE_URL},
			cloudExceptionStatuses = {CloudExceptionStatus.IMAGE_NOT_FOUND}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public MemberUpdateDto updateMyProfile(
			@LoginUserInfo UserSessionDto userSessionDto,
			@Valid @RequestBody MemberUpdateRequestDto memberUpdateRequestDto) {
		LocalDateTime now = LocalDateTime.now();
		memberService.updateProfile(userSessionDto.getUserId(), memberUpdateRequestDto);
		return memberQueryService.getMemberUpdateDto(userSessionDto.getUserId());
	}

	@DeleteMapping("/me/profile-image")
	@Operation(summary = "본인 프로필 이미지 삭제", description = "자신의 프로필 이미지를 삭제합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "ok"))
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHORIZED_MEMBER},
			memberExceptionStatuses = {MemberExceptionStatus.NOT_FOUND_MEMBER},
			cloudExceptionStatuses = {CloudExceptionStatus.IMAGE_NOT_FOUND}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public void deleteMyProfileImage(
			@LoginUserInfo UserSessionDto userSessionDto) {
		memberService.deleteProfileImage(userSessionDto.getUserId());
	}

	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{memberId}/profile")
	@Operation(summary = "멤버 프로필 조회", description = "특정 멤버의 프로필을 조회합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "ok"))
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHORIZED_MEMBER},
			memberExceptionStatuses = {MemberExceptionStatus.NOT_FOUND_MEMBER}
	)
	@Parameters({
			@Parameter(name = "memberId", description = "멤버 아이디", required = true)
	})
	public ProfileDto getMemberProfile(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long memberId
	) {
		return memberQueryService.getMemberProfile(userSessionDto.getUserId(), memberId);
	}

	@GetMapping("/me/oauthType")
	@Operation(summary = "본인 소셜 타입 조회", description = "자신의 소셜 계정이 어떤 provider에서 생성되었는지 조회합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "ok"))
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHORIZED_MEMBER},
			memberExceptionStatuses = {MemberExceptionStatus.NOT_FOUND_MEMBER}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public Map<String, String> getOauthType(
			@LoginUserInfo UserSessionDto userSessionDto) {
		OauthType oauthType = memberQueryService.getMemberOauthType(userSessionDto.getUserId());
		return Map.of(OAUTH_TYPE, oauthType.name());
	}
}
