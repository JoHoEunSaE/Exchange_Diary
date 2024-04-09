package org.johoeunsae.exchangediary.note.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoteFacadeServiceImpl implements NoteFacadeService {

	private final NoteService noteService;
	private final NoteQueryService noteQueryService;

	/*-----------------------------------------READ-----------------------------------------*/
	@Transactional(readOnly = true)
	@Override
	public MyNotePreviewPaginationDto getMyNotePreview(UserSessionDto sessionDto,
			Pageable pageable) {
		return noteQueryService.getMyNotePreviewsByMemberId(sessionDto.getUserId(), pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public MemberNotePreviewPaginationDto getNotePreview(UserSessionDto userSessionDto,
			Long memberId, Pageable pageable) {
		return noteQueryService.getMemberNotePreviews(userSessionDto.getUserId(), memberId,
				pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public NoteViewDto getNoteView(UserSessionDto userSessionDto, Long noteId) {
		return noteQueryService.getNoteView(userSessionDto.getUserId(), noteId);
	}

	@Transactional(readOnly = true)
	@Override
	public NotePreviewPaginationDto getPublicNotePreview(UserSessionDto userSessionDto,
			Pageable pageable) {
		return noteQueryService.getPublicNotePreviews(userSessionDto.getUserId(), pageable);
	}

	/*-----------------------------------------CUD-----------------------------------------*/
	@Transactional
	@Override
	public void updateNote(UserSessionDto sessionDto, Long noteId, NoteUpdateDto noteUpdateDto) {
		noteService.updateNote(sessionDto.getUserId(), noteId, noteUpdateDto.getTitle(),
				noteUpdateDto.getContent());
	}

	@Transactional
	@Override
	public void deleteNote(Long noteId, UserSessionDto sessionDto, LocalDateTime now) {
		noteService.deleteNote(noteId, sessionDto.getUserId(), now);
	}

	@Transactional
	@Override
	public void deleteNoteImages(UserSessionDto sessionDto, Long noteId,
			NoteImagesDeleteRequestDto noteImagesDeleteRequestDto) {
		noteService.deleteNoteImages(noteId, sessionDto.getUserId(),
				noteImagesDeleteRequestDto.getImageIndexes());
	}
}
