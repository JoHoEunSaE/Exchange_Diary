package org.johoeunsae.exchangediary.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.diary.service.DiaryFacadeService;
import org.johoeunsae.exchangediary.dto.DiaryCreateRequestDto;
import org.johoeunsae.exchangediary.dto.DiaryInvitationCodeRequestDto;
import org.johoeunsae.exchangediary.dto.DiaryMemberPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryNoteViewDto;
import org.johoeunsae.exchangediary.dto.DiaryPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryRecentNoteDto;
import org.johoeunsae.exchangediary.dto.DiaryUpdateRequestDto;
import org.johoeunsae.exchangediary.dto.InvitationCodeDto;
import org.johoeunsae.exchangediary.dto.NoteCreateRequestDto;
import org.johoeunsae.exchangediary.dto.NotePreviewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain.Fields;
import org.johoeunsae.exchangediary.utils.obfuscation.DataDecode;
import org.johoeunsae.exchangediary.utils.obfuscation.DataEncode;
import org.johoeunsae.exchangediary.utils.obfuscation.TargetMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/diaries")
@Tag(name = "일기장", description = "일기장 관련 API")
@Logging
public class DiaryController {

	private final DiaryFacadeService diaryFacadeService;

	// -------------------------Service-------------------------
	@Operation(summary = "일기장 생성", description = "일기장를 생성합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "created"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.EMPTY_COVER_COLOR_CODE,
					DiaryExceptionStatus.INVALID_COVER_COLOR_CODE,
					DiaryExceptionStatus.EMPTY_COVER_IMAGE,
					DiaryExceptionStatus.INVALID_COVER_IMAGE_EXTENSION,
					DiaryExceptionStatus.EXCEED_COVER_IMAGE_SIZE,
					DiaryExceptionStatus.DIARY_TITLE_LENGTH,
					DiaryExceptionStatus.INVALID_DIARY_TITLE,
					DiaryExceptionStatus.DIARY_GROUP_NAME_LENGTH,
					DiaryExceptionStatus.INVALID_DIARY_GROUP_NAME
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DiaryPreviewDto createDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@Valid @RequestBody DiaryCreateRequestDto dto
	) {
		return diaryFacadeService.createDiary(userSessionDto.getUserId(), dto);
	}

	@Operation(summary = "일기장 삭제", description = "일기장을 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NO_PERMISSION_DELETE
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{diaryId}")
	public void deleteDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId) {
		diaryFacadeService.deleteDiary(userSessionDto.getUserId(), diaryId);
	}

	@Operation(summary = "일기장 수정", description = "일기장을 수정합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.EMPTY_COVER_COLOR_CODE,
					DiaryExceptionStatus.INVALID_COVER_COLOR_CODE,
					DiaryExceptionStatus.EMPTY_COVER_IMAGE,
					DiaryExceptionStatus.INVALID_COVER_IMAGE_EXTENSION,
					DiaryExceptionStatus.EXCEED_COVER_IMAGE_SIZE,
					DiaryExceptionStatus.DIARY_TITLE_LENGTH,
					DiaryExceptionStatus.INVALID_DIARY_TITLE,
					DiaryExceptionStatus.DIARY_GROUP_NAME_LENGTH,
					DiaryExceptionStatus.INVALID_DIARY_GROUP_NAME,

					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NO_PERMISSION_EDIT
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PatchMapping(value = "/{diaryId}")
	public DiaryPreviewDto editDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@Valid @RequestBody DiaryUpdateRequestDto dto,
			@PathVariable Long diaryId) {
		return diaryFacadeService.editDiary(userSessionDto.getUserId(), diaryId, dto);
	}

	@Operation(summary = "일기장 초대 코드 조회", description = "일기장 초대 코드를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NO_PERMISSION_INVITE,
					DiaryExceptionStatus.NON_EXIST_DIARY
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{diaryId}/invitation")
	public InvitationCodeDto getDiaryInvitationCode(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId) {
		return diaryFacadeService.getDiaryInvitationCode(userSessionDto.getUserId(), diaryId);
	}

	@Operation(summary = "초대코드로 일기장 정보 가져오기", description = "초대코드로 일기장 정보를 가져옵니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.INVALID_INVITATION_CODE,
					DiaryExceptionStatus.NON_EXIST_DIARY
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping("/invitation")
	public DiaryPreviewDto getDiaryWithInvitationCode(
			@Valid @RequestBody DiaryInvitationCodeRequestDto diaryInvitationCodeRequestDto) {
		return diaryFacadeService.getDiaryWithInvitationCode(
				diaryInvitationCodeRequestDto.getCode());
	}

	@Operation(summary = "일기장 가입", description = "초대 코드로 일기장에 가입합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.INVALID_INVITATION_CODE,
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.EXCEED_MAX_DIARY_COUNT,
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping("/{diaryId}/invitation")
	public DiaryPreviewDto joinDiaryWithInvitation(
			@LoginUserInfo UserSessionDto userSessionDto,
			@Valid @RequestBody DiaryInvitationCodeRequestDto diaryInvitationCodeRequestDto,
			@PathVariable Long diaryId) {
		return diaryFacadeService.joinDiaryWithInvitation(userSessionDto.getUserId(), diaryId,
				diaryInvitationCodeRequestDto.getCode());
	}

	@Operation(summary = "일기장 주인 변경", description = "일기장 주인을 변경합니다. (일기장 방장만 가능합니다.)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NO_PERMISSION_CHANGE_DIARY_MASTER,
					DiaryExceptionStatus.NO_DIARY_MEMBER,
					DiaryExceptionStatus.ALREADY_MASTER_MEMBER
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER,
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PatchMapping("/{diaryId}/master/members/{targetMemberId}")
	public void changeDiaryMaster(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId,
			@PathVariable Long targetMemberId
	) {
		diaryFacadeService.changeDiaryMaster(userSessionDto.getUserId(), diaryId, targetMemberId);
	}

	@Operation(summary = "일기장 나가기", description = "일기장을 나갑니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
			@ApiResponse(responseCode = "403", description = "forbidden"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NO_DIARY_MEMBER
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER,
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{diaryId}/members/me")
	public void leaveDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId) {
		diaryFacadeService.leaveDiary(userSessionDto.getUserId(), diaryId);
	}

	@Operation(summary = "일기장의 멤버 추방", description = "일기장에서 멤버를 추방합니다. (방장만 가능합니다.)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
			@ApiResponse(responseCode = "403", description = "forbidden"),
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{diaryId}/members/{memberId}")
	public void kickDiaryMember(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId,
			@PathVariable Long memberId) {
		diaryFacadeService.kickDiaryMember(userSessionDto.getUserId(), diaryId, memberId);
	}

	@Operation(summary = "일기 쓰기", description = "일기장에 일기를 씁니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
			@ApiResponse(responseCode = "403", description = "forbidden"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.INVALID_NOTE_TITLE,
					DiaryExceptionStatus.NOTE_TITLE_LENGTH,
					DiaryExceptionStatus.INVALID_NOTE_CONTENT,
					DiaryExceptionStatus.NOTE_CONTENT_LENGTH,
					DiaryExceptionStatus.EXCEED_NOTE_IMAGE_SIZE,
					DiaryExceptionStatus.INVALID_NOTE_IMAGE_EXTENSION,

					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NO_PERMISSION_WRITE_NOTE
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PostMapping(value = "/{diaryId}/notes")
	@ResponseStatus(HttpStatus.CREATED)
	@DataEncode({
			@TargetMapping(clazz = NoteCreateRequestDto.class, fields = {"title", "content"})
	})
	public NotePreviewDto createNoteToDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId,
			@Valid @RequestBody NoteCreateRequestDto noteCreateRequestDto
	) {
		return diaryFacadeService.createNoteToDiary(userSessionDto.getUserId(), diaryId,
				noteCreateRequestDto);
	}

	@Operation(summary = "일기장의 일기 뜯어내기", description = "일기장에서 특정 일기를 뜯어냅니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
			@ApiResponse(responseCode = "403", description = "forbidden"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NO_PERMISSION_TEAR_OFF_NOTE,
					DiaryExceptionStatus.NOTE_NOT_BELONG_TO_DIARY
			},
			noteExceptionStatuses = {
					NoteExceptionStatus.NOT_FOUND_NOTE
			},
			memberExceptionStatuses = {
					MemberExceptionStatus.NOT_FOUND_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PatchMapping("/{diaryId}/notes/{noteId}")
	public void tearOffNoteFromDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId,
			@PathVariable Long noteId) {
		diaryFacadeService.tearOffNoteFromDiary(userSessionDto.getUserId(), diaryId, noteId);
	}

	//----------------------Query Service----------------------
	@Operation(summary = "일기장 목록 조회", description = "일기장 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NON_REGISTERED_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/members/me")
	@DataDecode
	public List<DiaryPreviewDto> getMyDiaries(
			@LoginUserInfo UserSessionDto userSessionDto) {
		return diaryFacadeService.getMyDiaries(userSessionDto.getUserId());
	}

	@Operation(summary = "일기장 정보 조회", description = "일기장을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NON_REGISTERED_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{diaryId}/members/me")
	@DataDecode
	public DiaryPreviewDto getMyDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("diaryId") Long diaryId
	) {
		return diaryFacadeService.getMyDiary(userSessionDto.getUserId(), diaryId);
	}


	@Operation(summary = "일기장 멤버 목록 조회", description = "일기장에 속한 멤버들을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{diaryId}/members")
	public List<DiaryMemberPreviewDto> getDiaryMembers(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId) {
		return diaryFacadeService.getMemberPreviewList(userSessionDto.getUserId(), diaryId);
	}


	@Operation(summary = "일기 열람", description = "일기장의 일기를 열람합니다. (prev, next 일기의 ID도 함께 제공합니다.)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NON_REGISTERED_MEMBER
			},
			noteExceptionStatuses = {
					NoteExceptionStatus.NOT_FOUND_NOTE
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{diaryId}/notes/{noteId}")
	@DataDecode
	public DiaryNoteViewDto getNoteFromDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId, @PathVariable Long noteId) {
		return diaryFacadeService.getNoteFromDiary(userSessionDto.getUserId(), diaryId, noteId);
	}

	@Operation(summary = "일기 프리뷰 목록 가져오기", description = "일기장의 일기 프리뷰 목록을 가져옵니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			diaryExceptionStatuses = {
					DiaryExceptionStatus.NON_EXIST_DIARY,
					DiaryExceptionStatus.NON_REGISTERED_MEMBER
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{diaryId}/notes")
	@DataDecode
	public NotePreviewPaginationDto getNotePreviewPaginationFromDiary(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable Long diaryId,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestParam(value = "sort", defaultValue = "DESC") Direction sort
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sort, Fields.id));
		return diaryFacadeService.getNotePreviewPaginationFromDiary(userSessionDto.getUserId(),
				diaryId, pageable);
	}

	@Operation(summary = "새 일기 조회", description = "내가 속한 일기장들의 새 일기들을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/members/me/new-notes")
	@DataDecode
	public List<DiaryRecentNoteDto> getMyDiariesNewNotes(
			@LoginUserInfo UserSessionDto userSessionDto
	) {
		return diaryFacadeService.getMyDiariesNewNotes(userSessionDto.getUserId());
	}
}
