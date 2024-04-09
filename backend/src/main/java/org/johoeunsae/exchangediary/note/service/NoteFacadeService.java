package org.johoeunsae.exchangediary.note.service;

import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface NoteFacadeService {

	MyNotePreviewPaginationDto getMyNotePreview(UserSessionDto sessionDto, Pageable pageable);

	MemberNotePreviewPaginationDto getNotePreview(UserSessionDto userSessionDto, Long memberId,
			Pageable pageable);

	NoteViewDto getNoteView(UserSessionDto userSessionDto, Long noteId);

	void updateNote(UserSessionDto sessionDto, Long noteId, NoteUpdateDto noteUpdateDto);

	void deleteNote(Long noteId, UserSessionDto sessionDto, LocalDateTime now);

	void deleteNoteImages(UserSessionDto sessionDto, Long noteId,
			NoteImagesDeleteRequestDto noteImagesDeleteRequestDto);

	NotePreviewPaginationDto getPublicNotePreview(UserSessionDto userSessionDto,
			Pageable pageable);
}
