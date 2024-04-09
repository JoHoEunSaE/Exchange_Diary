package org.johoeunsae.exchangediary.diary.service;

import org.johoeunsae.exchangediary.dto.DiaryCreateRequestDto;
import org.johoeunsae.exchangediary.dto.DiaryMemberPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryNoteViewDto;
import org.johoeunsae.exchangediary.dto.DiaryPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryUpdateRequestDto;
import org.johoeunsae.exchangediary.dto.InvitationCodeDto;
import org.johoeunsae.exchangediary.dto.DiaryRecentNoteDto;
import org.johoeunsae.exchangediary.dto.NoteCreateRequestDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.springframework.data.domain.Pageable;
import org.johoeunsae.exchangediary.dto.*;

import java.util.List;

public interface DiaryFacadeService {

	// -------------------------Service-------------------------
	DiaryPreviewDto createDiary(Long loginMemberId, DiaryCreateRequestDto dto);

	void deleteDiary(Long loginMemberId, Long diaryId);

	DiaryPreviewDto editDiary(Long loginMemberId, Long diaryId, DiaryUpdateRequestDto dto);

	DiaryPreviewDto joinDiaryWithInvitation(Long loginMemberId, Long diaryId, String code);

	DiaryPreviewDto getDiaryWithInvitationCode(String code);

	InvitationCodeDto getDiaryInvitationCode(Long loginMemberId, Long diaryId);

	void changeDiaryMaster(Long loginMemberId, Long diaryId, Long targetMemberId);

	void leaveDiary(Long loginMemberId, Long diaryId);

	void kickDiaryMember(Long loginMemberId, Long diaryId, Long memberId);

	NotePreviewDto createNoteToDiary(Long loginMemberId, Long diaryId, NoteCreateRequestDto dto);

	void tearOffNoteFromDiary(Long loginMemberId, Long diaryId, Long noteId);

	List<DiaryPreviewDto> getMyDiaries(Long loginMemberId);

	DiaryPreviewDto getMyDiary(Long loginMemberId, Long diaryId);

	List<DiaryRecentNoteDto> getMyDiariesNewNotes(Long loginMemberId);

	List<DiaryMemberPreviewDto> getMemberPreviewList(Long loginMemberId, Long diaryId);

	NotePreviewPaginationDto getNotePreviewPaginationFromDiary(Long loginMemberId, Long diaryId,
			Pageable pageable);

	DiaryNoteViewDto getNoteFromDiary(Long loginMemberId, Long diaryId, Long noteId);
}
