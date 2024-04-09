package org.johoeunsae.exchangediary.follow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.FollowExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.follow.service.FollowFacadeService;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.utils.OptionalGetter;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Tag(name = "팔로우", description = "팔로우 관련 API")
@RestController
@RequestMapping("/v1/follows")
@AllArgsConstructor
@Logging
public class FollowController {

	private final FollowFacadeService followFacadeService;

	@Operation(summary = "멤버 팔로워 조회", description = "특정 멤버의 팔로워 정보를 조회합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "팔로워 리스트 정상 반환"))
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER,
			}
	)
	@GetMapping("/followers/{memberId}")
	public MemberPreviewPaginationDto getFollowers(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return followFacadeService.getFollowers(wrapLoginId(userSessionDto), memberId,
				PageRequest.of(page, size));
	}

	@Operation(summary = "멤버 팔로잉 조회", description = "특정 멤버의 팔로잉 정보를 조회합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "팔로잉 리스트 정상 반환"))
	@ApiErrorCodeExample(
			memberExceptionStatuses = {MemberExceptionStatus.NOT_FOUND_MEMBER}
	)
	@GetMapping("/followings/{memberId}")
	public MemberPreviewPaginationDto getFollowings(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return followFacadeService.getFollowings(wrapLoginId(userSessionDto), memberId,
				PageRequest.of(page, size));
	}

	@Operation(summary = "팔로우 취소", description = "특정 멤버의 팔로우를 취소합니다.")
	@ApiResponses(@ApiResponse(responseCode = "200", description = "멤버 팔로우 취소"))
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHENTICATED_MEMBER},
			memberExceptionStatuses = {MemberExceptionStatus.NOT_FOUND_MEMBER})
	@DeleteMapping("/{memberId}")
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public void deleteFollow(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId) {
		followFacadeService.deleteFollow(userSessionDto.getUserId(), memberId);
	}

	@Operation(summary = "팔로우 등록", description = "특정 멤버를 팔로우 합니다.")
	@ApiResponses(@ApiResponse(responseCode = "201", description = "멤버 팔로우"))
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHENTICATED_MEMBER},
			memberExceptionStatuses = {MemberExceptionStatus.NOT_FOUND_MEMBER},
			followExceptionStatuses = {FollowExceptionStatus.SELF_FOLLOW,
					FollowExceptionStatus.DOUBLE_FOLLOW}
	)
	@PostMapping("/{memberId}")
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public void createFollow(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId) {
		LocalDateTime now = LocalDateTime.now();
		followFacadeService.createFollow(userSessionDto.getUserId(), memberId, now);
	}

	private Optional<Long> wrapLoginId(UserSessionDto userSessionDto) {
		return OptionalGetter.get(userSessionDto, UserSessionDto::getUserId);
	}
}
