package org.johoeunsae.exchangediary.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.ReportRequestDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.ReportExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.report.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "신고", description = "신고 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/report")
@Logging
public class ReportController {

	private final ReportService reportService;

	@Operation(summary = "신고하기", description = "특정 멤버를 신고합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@ApiErrorCodeExample(
			memberExceptionStatuses = {
					org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER
			},
			reportExceptionStatuses = {
					ReportExceptionStatus.DUPLICATE_REPORT,
					ReportExceptionStatus.CANNOT_REPORT_MYSELF
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping("/members/{memberId}")
	public void createMemberReport(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId,
			@RequestBody ReportRequestDto reportRequestDto
	) {
		reportService.reportMember(userSessionDto.getUserId(), memberId, reportRequestDto);
	}

	@Operation(summary = "신고하기", description = "특정 일기를 신고합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@ApiErrorCodeExample(
			noteExceptionStatuses = {
					org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus.NOT_FOUND_NOTE
			},
			reportExceptionStatuses = {
					ReportExceptionStatus.DUPLICATE_REPORT,
					ReportExceptionStatus.CANNOT_REPORT_MYSELF
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping("/notes/{noteId}")
	public void createNoteReport(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId,
			@RequestBody ReportRequestDto reportRequestDto
	) {
		reportService.reportNote(userSessionDto.getUserId(), noteId, reportRequestDto);
	}
}
