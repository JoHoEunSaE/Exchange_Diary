package org.johoeunsae.exchangediary.notice.repository;

import org.johoeunsae.exchangediary.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query("SELECT n " +
			"FROM Notice n " +
			"WHERE n.receiver.id = :memberId")
	List<Notice> findAllByMemberId(@Param("memberId") Long memberId);
}
