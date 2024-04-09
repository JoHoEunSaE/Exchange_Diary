package org.johoeunsae.exchangediary.diary.repository;

import java.util.List;
import org.johoeunsae.exchangediary.diary.domain.CoverImage;
import org.johoeunsae.exchangediary.dto.DiaryNoteMemberDto;
import org.johoeunsae.exchangediary.dto.DiaryRecentNoteDto;

public interface DiaryRepositoryCustom {

	void deleteCoverImage(CoverImage coverImage);

	/**
	 * 특정 멤버의 모든 다이어리 정보와 다이어리에 포함된 일기 중 최근 1개를 조회합니다.
	 *
	 * @param loginMemberId  다이어리를 조회할 멤버의 ID
	 * @return 다이어리와 각 다이어리별 최근 일기 1개의 정보
	 */
	List<DiaryRecentNoteDto> getRecentDiaryAndNoteWithAuthor(Long loginMemberId);

	/**
	 * 특정 멤버의 모든 다이어리 정보와 다이어리에 포함된 일기 중 최근 1개를 조회합니다.
	 *
	 * @param loginMemberId  다이어리를 조회할 멤버의 ID
	 * @return 다이어리와 각 다이어리별 최근 일기 1개의 정보
	 */
	List<DiaryNoteMemberDto> getDiaryNoteMembers(Long loginMemberId);
}

