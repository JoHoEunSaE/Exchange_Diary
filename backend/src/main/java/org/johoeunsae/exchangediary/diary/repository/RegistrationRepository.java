package org.johoeunsae.exchangediary.diary.repository;

import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long>, RegistrationRepositoryCustom {

	/**
	 * 해당 다이어리에 등록된 Registeration 목록을 조회합니다.
	 *
	 * @param diaryId 다이어리 ID
	 * @return Registration 목록
	 */
	@Query("SELECT r FROM Registration r WHERE r.diary.id = :diaryId")
	List<Registration> findByDiaryId(@Param("diaryId") Long diaryId);

	/**
	 * 해당 멤버가 등록된 Registeration 목록을 조회합니다.
	 *
	 * @param loginMemberId 멤버 ID
	 * @return Registration 목록
	 */
	@Query("SELECT r FROM Registration r WHERE r.member.id = :loginMemberId")
	List<Registration> findAllByMemberId(Long loginMemberId);

	@Query("SELECT r FROM Registration r WHERE r.member.id = :loginMemberId AND r.diary.id = :diaryId")
	Registration findByMemberIdAndDiaryId(Long loginMemberId, Long diaryId);
}
