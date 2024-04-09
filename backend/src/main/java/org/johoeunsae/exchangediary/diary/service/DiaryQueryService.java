package org.johoeunsae.exchangediary.diary.service;

import java.util.List;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.dto.DiaryNoteViewDto;
import org.johoeunsae.exchangediary.dto.DiaryPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryRecentNoteDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * {@link Diary} 쿼리 서비스 인터페이스입니다.
 */
public interface DiaryQueryService {

	//----------------------Query Service----------------------

	/**
	 * 내가 속한 일기장 목록을 가져옵니다.
	 *
	 * @return 내가 속한 일기장 목록
	 */
	List<DiaryPreviewDto> getMyDiaries(Long loginMemberId);

	/**
	 * 일기장 하나의 정보를 가져옵니다.
	 *
	 * @param loginMemberId 로그인한 멤버의 ID
	 * @param diaryId 일기장 ID
	 * @return 일기장 하나의 정보
	 */
	DiaryPreviewDto getMyDiary(Long loginMemberId, Long diaryId);

	/**
	 * 일기장의 정보를 가져옵니다.
	 *
	 * @param diaryId 일기장 ID
	 * @return 일기장의 정보
	 */
	DiaryPreviewDto getDiaryInfo(Long diaryId);

	/**
	 * 내가 속한 일기장들과 그 일기장들의 최신 일기를 가져옵니다.
	 *
	 * @return 내가 속한 일기장들의 최신 일기
	 */
	List<DiaryRecentNoteDto> getMyDiariesNewNotes(Long loginMemberId);

	/**
	 * 일기장의 멤버 목록을 가져옵니다.
	 * <p>
	 * 내가 속한 일기장이어야 합니다.
	 *
	 * @param diaryId 일기장 ID
	 * @return 일기장의 멤버 목록
	 */
	List<MemberPreviewDto> getDiaryMembers(Long loginUserId, Long diaryId);

	/**
	 * 일기장의 특정 일기를 가져옵니다.
	 * <p>
	 * 내가 속한 일기장이어야 합니다.
	 *
	 * @param diaryId 일기장 ID
	 * @param noteId  일기 ID
	 * @return 일기장의 특정 일기
	 */
	DiaryNoteViewDto getNoteFromDiary(Long loginMemberId, Long diaryId, Long noteId);

	/**
	 * 일기장의 일기 페이지네이션을 가져옵니다.
	 * <p>
	 * 내가 속한 일기장이어야 합니다.
	 *
	 * @param diaryId 일기장 ID
	 * @param page    페이지 번호
	 * @param size    페이지 크기
	 * @return 일기장의 일기 목록
	 */
	Page<NotePreviewDto> getNotePaginationFromDiary(Long diaryId, Integer page,
			Integer size);

	/**
	 * 일기장의 일기 프리뷰 페이지네이션을 가져옵니다.
	 *
	 * @param diaryId 일기장 ID
	 * @param pageable 페이지 요청
	 * @return
	 */
	NotePreviewPaginationDto getNotePreviewPaginationFromDiary(Long loginUserId,
			Long diaryId, Pageable pageable);
}
