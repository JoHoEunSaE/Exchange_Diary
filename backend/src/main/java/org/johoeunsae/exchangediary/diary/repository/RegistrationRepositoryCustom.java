package org.johoeunsae.exchangediary.diary.repository;

import java.util.List;
import org.johoeunsae.exchangediary.dto.DiaryMemberDto;

public interface RegistrationRepositoryCustom {

	/**
	 * 해당 다이어리에 등록된 멤버 목록을 조회합니다.
	 *
	 * @param diaryId 다이어리 ID
	 * @param memberId 멤버 ID
	 * @return DiaryMemberDto 목록
	 */
	List<DiaryMemberDto> getDiaryMembers(Long diaryId, Long memberId);
}
