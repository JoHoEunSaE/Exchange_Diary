package org.johoeunsae.exchangediary.follow.repository;

import org.johoeunsae.exchangediary.follow.domain.Follow;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, MemberCompositeKey> {
	@Query("select count (*) from Follow f where f.id.memberId = :fromMemberId")
	Integer countByFromMemberId(@Param("fromMemberId") Long fromMemberId);

	@Query("select count (*) from Follow f where f.id.targetMemberId = :toMemberId")
	Integer countByToMemberId(@Param("toMemberId") Long toMemberId);
}
