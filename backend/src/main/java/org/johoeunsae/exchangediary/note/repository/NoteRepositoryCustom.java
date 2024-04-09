package org.johoeunsae.exchangediary.note.repository;

import org.johoeunsae.exchangediary.note.domain.Note;
import org.springframework.data.domain.Page;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface NoteRepositoryCustom {

	/**
	 * 일기가 속한 일기장의 그룹 이름을 반환합니다.
	 *
	 * @param noteId 일기장 ID
	 * @return
	 */
	String findGroupNameByNoteId(Long noteId);

	/**
	 * beforeDiaryId에 속한 일기들의 일기장 ID를 afterDiaryId로 변경합니다.
	 *
	 * @param beforeDiaryId 변경 전 일기장 ID
	 * @param afterDiaryId  변경 후 일기장 ID
	 */
	void updateDiaryIdByDiaryId(Long beforeDiaryId, Long afterDiaryId);

	/**
	 * 해당 노트의 다이어리 ID를 변경합니다.
	 *
	 * @param noteId       변경 대상이 되는 노트의 ID
	 * @param afterDiaryId 변경할 다이어리의 ID
	 */
	void updateDiaryIdByNoteId(Long noteId, Long afterDiaryId);

	/**
	 * 다이어리에 속한 현재 노트ID를 기준으로 이전 노트를 가져옵니다.
	 *
	 * @param note    현재 노트
	 * @param diaryId 현재 노트가 속한 다이어리 ID
	 * @return 이전 노트
	 */
	Optional<Note> findPreviousNoteByPresentNote(Long loginUserId, Note note, Long diaryId);

	/**
	 * 다이어리에 속한 현재 노트ID를 기준으로 다음 노트를 가져옵니다.
	 *
	 * @param note    현재 노트
	 * @param diaryId 현재 노트가 속한 다이어리 ID
	 * @return 다음 노트
	 */
	Optional<Note> findNextNoteByPresentNote(Long loginUserId, Note note, Long diaryId);

	/**
	 * 공개된 일기 목록을 조회합니다.
	 *
	 * @param pageable 페이지네이션
	 * @return 공개된 일기 페이지
	 */
	Page<Note> findPublicNotes(Long loginUserId, Pageable pageable);

	/**
	 * 특정 멤버의 일기들의 일기장 ID를 변경합니다.
	 *
	 * @param memberId      변경할 멤버의 ID
	 * @param beforeDiaryId 변경 전 일기장 ID
	 * @param afterDiaryId  변경 후 일기장 ID
	 */
	void updateDiaryIdByMemberId(Long memberId, Long beforeDiaryId, Long afterDiaryId);

	/**
	 * DiaryId로 Note 목록을 조회합니다.
	 * 볼 수 있는 note들(현재 PRIVATE, PUBLIC)만 조회합니다.
	 *
	 * @param diaryId 다이어리 ID
	 * @param pageable 페이지네이션
	 * @return 다이어리에 속한 Note 목록
	 */
	Page<Note> findVisibleNotesByDiaryId(Long loginUserId, Long diaryId, Pageable pageable);

	/**
	 * MemberId로 Note 목록을 조회합니다.
	 * 볼 수 있는 note들(현재 PRIVATE, PUBLIC)만 조회합니다.
	 *
	 * @param memberId 멤버 ID
	 * @param pageable 페이지네이션
	 * @return 멤버가 작성한 Note 목록
	 */
	Page<Note> findVisibleNotesByMemberId(Long loginUserId, Long memberId, Pageable pageable);

	/**
	 * NoteId로 Note를 조회합니다.
	 * 볼 수 있는 note(현재 PRIVATE, PUBLIC)만 조회합니다.
	 *
	 * @param noteId 노트 ID
	 * @return 노트
	 */
	Optional<Note> findVisibleNoteByNoteId(Long loginUserId, Long noteId);
}
