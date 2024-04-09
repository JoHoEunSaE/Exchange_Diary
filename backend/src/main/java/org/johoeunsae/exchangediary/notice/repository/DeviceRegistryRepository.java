package org.johoeunsae.exchangediary.notice.repository;

import org.johoeunsae.exchangediary.notice.domain.DeviceRegistry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRegistryRepository extends JpaRepository<DeviceRegistry, Long> {

	@Query("SELECT d.token " +
			"FROM DeviceRegistry d " +
			"WHERE d.member.id = :memberId")
	List<String> findByMemberId(@Param("memberId") Long memberId);

	@EntityGraph(attributePaths = {"member"})
	@Query("SELECT d " +
			"FROM DeviceRegistry d ")
	List<DeviceRegistry> findAll();


	@Query("SELECT d " +
			"FROM DeviceRegistry d " +
			"WHERE d.member.id = :memberId " +
			"ORDER BY d.createdAt ASC")
	List<DeviceRegistry> findByMemberIdOrderByCreatedAt(Long memberId);
}
