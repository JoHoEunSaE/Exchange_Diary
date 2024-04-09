package org.johoeunsae.exchangediary.note.service;

import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.dto.NoteImageCreateDto;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteService {

	Note createNoteToDiary(Long userId, String title, String content, VisibleScope visibleScope,
	                       List<NoteImageCreateDto> imageData, LocalDateTime now, Diary diary);

	void updateNote(Long userId, Long noteId, String title, String content);

	void deleteNote(Long noteId, Long memberId, LocalDateTime now);

	void deleteNoteImages(Long noteId, Long userId, List<Integer> imageIndexes);

	/**
	 * @formatter:off
	 * 일기장에 속한 모든 일기들을 떼어냅니다.
	 * 일기장에 속한 모든 일기들의 diaryId를 default {@link Diary#DEFAULT_DIARY_ID} 값으로 변경합니다.
	 * @formatter:on
	 *
	 * @param diaryId 일기장 ID
	 */
	void tearOffNotesFromDiary(Long diaryId);

	/**
	 * 일기장에서 특정 일기를 떼어냅니다.
	 *
	 * @param memberId 멤버 ID
	 * @param diaryId  일기장 ID
	 * @param noteId   떼어낼 일기 ID
	 */
	void tearOffNoteFromDiary(Long memberId, Long diaryId, Long noteId);

	/**
	 * 일기장에서 특정 멤버의 일기들을 떼어냅니다.
	 *
	 * @param memberId 멤버 ID
	 * @param diaryId  일기장 ID
	 */
	void tearOffMemberNotesFromDiary(Long memberId, Long diaryId);
}
