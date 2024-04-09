package org.johoeunsae.exchangediary.note.service;

import org.johoeunsae.exchangediary.dto.MemberNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MyNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.springframework.data.domain.Pageable;

public interface NoteQueryService {

	MyNotePreviewPaginationDto getMyNotePreviewsByMemberId(Long loginUserId, Pageable pageable);

	MemberNotePreviewPaginationDto getMemberNotePreviews(Long loginUserId, Long targetId,
			Pageable pageable);

	NoteViewDto getNoteView(Long loginUserId, Long noteId);

	NotePreviewPaginationDto getPublicNotePreviews(Long userId, Pageable pageable);

	NotePreviewPaginationDto getNotePreviewsByDiaryId(Long loginUserId, Long diaryId,
			Pageable pageable);
}
