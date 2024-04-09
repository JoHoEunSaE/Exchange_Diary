package org.johoeunsae.exchangediary.block.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.block.service.BlockQueryService;
import org.johoeunsae.exchangediary.block.service.BlockService;
import org.johoeunsae.exchangediary.dto.BlockedUserPaginationDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.BlockExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "차단", description = "차단 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/blocks")
@Logging
public class BlockController {

	private final BlockService blockService;
	private final BlockQueryService blockQueryService;

	@Operation(summary = "차단하기", description = "특정 멤버를 차단합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "차단 멤버 추가"),

	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			},
			blockExceptionStatuses = {
					BlockExceptionStatus.ALREADY_BLOCKED_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{memberId}")
	public void createBlock(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId) {
		blockService.blockUser(userSessionDto.getUserId(), memberId);
	}

	@Operation(summary = "차단 해제", description = "자신이 차단한 멤버를 차단 해제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "멤버 차단 해제"),

	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			},
			blockExceptionStatuses = {
					BlockExceptionStatus.NOT_FOUND_BLOCK
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{memberId}")
	public void deleteBlock(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId) {
		blockService.unblockUser(userSessionDto.getUserId(), memberId);
	}

	@Operation(summary = "차단 멤버 목록 조회", description = "자신이 차단한 멤버 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "멤버가 차단 멤버 리스트 정상 반환")
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping
	public BlockedUserPaginationDto getBlockedUserList(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return blockQueryService.getBlockedUsers(userSessionDto.getUserId(),
				PageRequest.of(page, size));
	}
}
