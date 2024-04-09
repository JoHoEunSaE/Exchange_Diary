package org.johoeunsae.exchangediary.diary.repository;

import org.johoeunsae.exchangediary.diary.domain.CoverColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoverColorRepository extends JpaRepository<CoverColor, Long> {

	/**
	 * diaryId로 CoverColor를 삭제합니다.
	 *
	 * @param diaryId 삭제할 CoverColor의 diaryId
	 */
	@Modifying
	@Query("delete from CoverColor c where c.diary.id = :diaryId")
	void deleteByDiaryId(@Param("diaryId") Long diaryId);

	/**
	 * diaryId로 ColorCode를 변경합니다.
	 *
	 * @param diaryId        변경할 CoverColor의 diaryId
	 * @param coverColorCode 변경할 CoverColor의 색상 코드
	 */
	@Modifying
	@Query("update CoverColor c set c.colorCode = :coverColorCode where c.diary.id = :diaryId")
	void updateCoverColorByDiaryId(Long diaryId, String coverColorCode);
}
