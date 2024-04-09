package org.johoeunsae.exchangediary.diary.service;

import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NON_EXIST_DIARY;
import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NON_REGISTERED_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NOTE_NOT_BELONG_TO_DIARY;
import static org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus.NOT_FOUND_NOTE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.dto.DiaryNoteMemberDto;
import org.johoeunsae.exchangediary.dto.DiaryNoteViewDto;
import org.johoeunsae.exchangediary.dto.DiaryPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryRecentNoteDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.mapper.DiaryMapper;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.johoeunsae.exchangediary.note.service.NoteQueryService;
import org.johoeunsae.exchangediary.utils.QueryService;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link Diary} 쿼리 서비스입니다.
 * <p>
 * Read-Only 서비스이며, CUD는 {@link DiaryService}를 사용합니다.
 */
@QueryService
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class DiaryQueryServiceImpl implements DiaryQueryService {

	private final DiaryRepository diaryRepository;
	private final RegistrationRepository registrationRepository;
	private final NoteQueryService noteQueryService;
	private final NoteRepository noteRepository;
	private final DiaryMapper diaryMapper;
	private final ImageService imageService;

	@Override
	public List<DiaryPreviewDto> getMyDiaries(Long loginMemberId) {
		List<Registration> registrations = registrationRepository.findAllByMemberId(loginMemberId);
		return registrations.stream()
				.map(registration ->
					diaryMapper.toDiaryPreviewDto(registration.getDiary(), getCoverData(registration.getDiary())))
							.collect(Collectors.toList());
	}

	@Override
	public DiaryPreviewDto getMyDiary(Long loginMemberId, Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId).orElseThrow(NON_EXIST_DIARY::toDomainException);
		if (!diary.isDiaryMember(loginMemberId)) {
			throw NON_REGISTERED_MEMBER.toServiceException();
		}
		return diaryMapper.toDiaryPreviewDto(diary, getCoverData(diary));
	}

	@Override
	public DiaryPreviewDto getDiaryInfo(Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId).orElseThrow(NON_EXIST_DIARY::toDomainException);
		return diaryMapper.toDiaryPreviewDto(diary, getCoverData(diary));
	}

	@Override
	public List<DiaryRecentNoteDto> getMyDiariesNewNotes(Long loginMemberId){
		List<DiaryNoteMemberDto> diaryNoteMembers = diaryRepository.getDiaryNoteMembers(loginMemberId);
		return diaryNoteMembers.stream().map(
				diaryNoteMemberVO -> new DiaryRecentNoteDto(diaryNoteMemberVO,
						imageService.getImageUrl(diaryNoteMemberVO.getMember().getProfileImageUrl()),
						imageService.getImageUrl(diaryNoteMemberVO.getNote().getThumbnailUrl()))
		).collect(Collectors.toList());
	}

	@Override
	public List<MemberPreviewDto> getDiaryMembers(Long loginUserId, Long diaryId) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public DiaryNoteViewDto getNoteFromDiary(Long loginMemberId, Long diaryId, Long noteId) {
		Diary diary = diaryRepository.findById(diaryId).orElseThrow(NON_EXIST_DIARY::toDomainException);
		if (!diary.isDiaryMember(loginMemberId)) {
			throw NON_REGISTERED_MEMBER.toServiceException();
		}
		NoteViewDto noteViewDto = noteQueryService.getNoteView(loginMemberId, noteId);
		if (!diaryId.equals(noteViewDto.getDiaryId())) {
			throw NOTE_NOT_BELONG_TO_DIARY.toServiceException();
		}
		Long prevNoteId = getPrevNoteId(loginMemberId, diaryId, noteId);
		Long nextNoteId = getNextNoteId(loginMemberId, diaryId, noteId);
		return diaryMapper.toDiaryNoteViewDto(noteViewDto, noteViewDto.isLiked(),
				noteViewDto.isBookmarked(), noteViewDto.isBlocked(), prevNoteId, nextNoteId);
	}

	@Override
	public Page<NotePreviewDto> getNotePaginationFromDiary(Long diaryId, Integer page,
			Integer size) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public NotePreviewPaginationDto getNotePreviewPaginationFromDiary(Long loginUserId, Long diaryId, Pageable pageable) {
		diaryRepository.findById(diaryId).orElseThrow(NON_EXIST_DIARY::toDomainException);
		return noteQueryService.getNotePreviewsByDiaryId(loginUserId, diaryId, pageable);
	}

	private String getCoverData(Diary diary) {
		String imageData = null;
		if (diary.getCoverType() == CoverType.COLOR)
			imageData = diary.getCoverColor().getColorCode();
		else if (diary.getCoverType() == CoverType.IMAGE)
			imageData = imageService.getImageUrl(diary.getCoverImage().getImageUrl());
		return imageData;
	}

	private Long getPrevNoteId(Long loginUserId, Long diaryId, Long noteId) {
		Note note = noteRepository.findVisibleNoteByNoteId(loginUserId, noteId).orElseThrow(NOT_FOUND_NOTE::toDomainException);
		Optional<Note> prevNote = noteRepository.findPreviousNoteByPresentNote(loginUserId, note, diaryId);
		return prevNote.map(IdentityIdDomain::getId).orElse(null);
	}

	private Long getNextNoteId(Long loginUserId, Long diaryId, Long noteId) {
		Note note = noteRepository.findVisibleNoteByNoteId(loginUserId, noteId).orElseThrow(NOT_FOUND_NOTE::toDomainException);
		Optional<Note> nextNote = noteRepository.findNextNoteByPresentNote(loginUserId, note, diaryId);
		return nextNote.map(IdentityIdDomain::getId).orElse(null);
	}
}
