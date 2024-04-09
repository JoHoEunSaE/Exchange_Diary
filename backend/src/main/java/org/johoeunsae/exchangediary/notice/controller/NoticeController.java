package org.johoeunsae.exchangediary.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.NoticeDeleteRequestDto;
import org.johoeunsae.exchangediary.dto.NoticeDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoticeExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.notice.service.NoticeFacadeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림", description = "알림 관련 API")
@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
@Logging
public class NoticeController {

	private final NoticeFacadeService noticeFacadeService;

	@Operation(summary = "알림 가져오기", description = "유저가 받은 알림들을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "알림 가져오기 성공"),
	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			},
			noticeExceptionStatuses = {
					NoticeExceptionStatus.NOT_BELONGED
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping
	public List<NoticeDto> getNotice(
			@LoginUserInfo UserSessionDto userSessionDto) {
		return noticeFacadeService.getAllNotices(userSessionDto.getUserId());
	}

	@Operation(summary = "알림 삭제", description = "유저가 지정한 알림을 삭제합니다. 삭제할 알림들의 ID 배열을 body에 담아서 보내주세요.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "알림 삭제 성공"),

	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping("/delete")
	public void deleteNotice(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestBody NoticeDeleteRequestDto requestDto) {
		noticeFacadeService.deleteNotices(userSessionDto.getUserId(), requestDto);
	}

	// TODO: 관리자가 공지사항을 발행하는 API
}
