package org.johoeunsae.exchangediary.diary.repository;

import org.johoeunsae.exchangediary.diary.domain.CoverImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoverImageRepository extends JpaRepository<CoverImage, Long> {

	/**
	 * diaryId로 CoverImage를 삭제합니다.
	 *
	 * @param diaryId 삭제할 CoverImage의 diaryId
	 */
	@Modifying
	@Query("delete from CoverImage c where c.diary.id = :diaryId")
	void deleteByDiaryId(@Param("diaryId") Long diaryId);

	/**
	 * diaryId로 ImageUrl을 변경합니다.
	 *
	 * @param diaryId  변경할 CoverImage의 diaryId
	 * @param imageUrl 변경할 CoverImage의 이미지 URL
	 */
	@Modifying
	@Query("update CoverImage c set c.imageUrl = :imageUrl where c.diary.id = :diaryId")
	void updateCoverImageByDiaryId(Long diaryId, String imageUrl);
}
