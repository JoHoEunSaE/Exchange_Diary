package org.johoeunsae.exchangediary.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.bookmark.service.BookmarkQueryService;
import org.johoeunsae.exchangediary.bookmark.service.BookmarkService;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.BookmarkExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "북마크", description = "북마크 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookmarks")
@Logging
public class BookmarkController {

	private final BookmarkService bookmarkService;
	private final BookmarkQueryService bookmarkQueryService;

	@Operation(summary = "내 북마크 목록 가져오기", description = "본인이 북마크한 일기 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "본인의 북마크 리스트 정상 반환"),
	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping
	public NotePreviewPaginationDto getBookmarkList(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return bookmarkQueryService.getBookmarkList(userSessionDto.getUserId(),
				userSessionDto.getUserId(), pageable);
	}

	@Operation(summary = "북마크 추가", description = "특정 일기를 북마크합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "북마크 추가"),
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
	public void createBookmark(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId
	) {
		bookmarkService.createBookmark(userSessionDto.getUserId(), noteId);
	}

	@Operation(summary = "북마크 삭제", description = "특정 일기를 북마크 해제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "북마크 해제"),
	})
	@ApiErrorCodeExample(
			bookmarkExceptionStatuses = {
					BookmarkExceptionStatus.NOT_FOUND_BOOKMARK,
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
	public void deleteBookmark(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId
	) {
		bookmarkService.deleteBookmark(userSessionDto.getUserId(), noteId);
	}
}
