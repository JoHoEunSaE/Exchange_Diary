package org.johoeunsae.exchangediary.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.LikeExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus;
import org.johoeunsae.exchangediary.like.service.LikeService;
import org.johoeunsae.exchangediary.log.Logging;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좋아요", description = "좋아요 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/likes")
@Logging
public class LikeController {

	private final LikeService likeService;

	@Operation(summary = "좋아요 하기", description = "특정 노트에 좋아요를 합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "좋아요"),

	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			},
			noteExceptionStatuses = {
					NoteExceptionStatus.NOT_FOUND_NOTE
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{noteId}")
	public void createLike(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId) {
		likeService.createLike(noteId, userSessionDto.getUserId());
	}

	@Operation(summary = "좋아요 취소", description = "특정 노트에 좋아요를 취소합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "좋아요 취소"),

	})
	@ApiErrorCodeExample(
			likeExceptionStatuses = {
					LikeExceptionStatus.NOT_FOUND_LIKE
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			},
			noteExceptionStatuses = {
					NoteExceptionStatus.NOT_FOUND_NOTE
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{noteId}")
	public void deleteLike(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId) {
		likeService.deleteLike(noteId, userSessionDto.getUserId());
	}
}
