package org.johoeunsae.exchangediary.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.MemberNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MyNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteImagesDeleteRequestDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteUpdateDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.note.service.NoteFacadeService;
import org.johoeunsae.exchangediary.utils.obfuscation.DataDecode;
import org.johoeunsae.exchangediary.utils.obfuscation.DataEncode;
import org.johoeunsae.exchangediary.utils.obfuscation.TargetMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "일기", description = "Note API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notes")
@Logging
public class NoteController {

	private final NoteFacadeService noteFacadeService;

	@Operation(summary = "자신이 쓴 일기 목록 조회", description = "자신이 쓴 일기의 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "일기 목록 조회에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/members/me")
	@DataDecode
	public MyNotePreviewPaginationDto getMyNotePreview(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return noteFacadeService.getMyNotePreview(userSessionDto, PageRequest.of(page, size));
	}

	@Operation(summary = "공개된 일기 조회", description = "PUBLIC으로 설정된 일기의 프리뷰들을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "공개된 일기 조회에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/public")
	@DataDecode
	public NotePreviewPaginationDto getPublicNotePreview(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return noteFacadeService.getPublicNotePreview(userSessionDto, pageable);
	}

	@Operation(summary = "특정 멤버가 쓴 일기 목록 조회", description = "특정 멤버가 쓴 일기의 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "일기 목록 조회에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/members/{memberId}")
	@DataDecode
	public MemberNotePreviewPaginationDto getMemberNotePreview(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("memberId") Long memberId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return noteFacadeService.getNotePreview(userSessionDto, memberId, pageable);
	}

	@Operation(summary = "일기 상세 조회", description = "일기 상세 내용을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "일기 상세 조회에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@Parameters({
			@Parameter(name = "noteId", description = "일기 아이디", required = true, example = "1")
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/{noteId}")
	@DataDecode
	public NoteViewDto getNote(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId
	) {
		return noteFacadeService.getNoteView(userSessionDto, noteId);
	}

	@Operation(summary = "일기 내용 업데이트", description = "일기 내용을 업데이트합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "일기 내용 업데이트에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@Parameters({
			@Parameter(name = "noteId", description = "일기 아이디", required = true, example = "1")
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@PatchMapping("/{noteId}")
	@DataEncode({
			@TargetMapping(clazz = NoteUpdateDto.class, fields = {NoteUpdateDto.Fields.title,
					NoteUpdateDto.Fields.content})
	})
	public void updateNote(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId,
			@RequestBody @Valid NoteUpdateDto noteUpdateDto
	) {
		noteFacadeService.updateNote(userSessionDto, noteId, noteUpdateDto);
	}

	@Operation(summary = "일기 삭제", description = "일기를 삭제합니다. 일기에 올라간 이미지도 함께 삭제됩니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "일기 삭제에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@Parameters({
			@Parameter(name = "noteId", description = "일기 아이디", required = true, example = "1")
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{noteId}")
	public void deleteNote(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId
	) {
		noteFacadeService.deleteNote(noteId, userSessionDto, LocalDateTime.now());
	}

	@Operation(summary = "일기 이미지 삭제", description = "일기 이미지를 삭제합니다. 삭제할 이미지들의 index를 배열로 전달해야 합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "일기 이미지 삭제에 성공합니다."),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(hidden = true)))
	})
	@Parameters({
			@Parameter(name = "noteId", description = "일기 아이디", required = true, example = "1")
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@DeleteMapping("/{noteId}/note-images")
	public void deleteNoteImages(
			@LoginUserInfo UserSessionDto userSessionDto,
			@PathVariable("noteId") Long noteId,
			@RequestBody NoteImagesDeleteRequestDto noteImagesDeleteRequestDto
	) {
		noteFacadeService.deleteNoteImages(userSessionDto, noteId, noteImagesDeleteRequestDto);
	}
}
