package org.johoeunsae.exchangediary.member.repository;

import org.johoeunsae.exchangediary.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	@Query("SELECT m FROM Member m WHERE m.nickname = :nickname AND m.deletedAt IS NULL")
	Optional<Member> findByNickname(@Param("nickname") String nickname);

	Optional<Member> findMemberByEmail(@Param("email") String email);

	@Query("SELECT m FROM Member m WHERE m.deletedAt IS NULL AND m.id = :id")
	Optional<Member> findMemberByDeletedAtIsNotNullAndId(@Param("id") Long id);

	@Query("SELECT m FROM Member m WHERE m.nickname LIKE %:nickname% AND m.deletedAt IS NULL")
	Page<Member> findByPartialNickname(@Param("nickname") String nickname, Pageable pageable);

  @Query("SELECT m " +
			"FROM Member m " +
			"JOIN FETCH m.deviceRegistries " +
			"WHERE m.id IN :memberIds")
	List<Member> findAllByIdWithDeviceRegistries(List<Long> memberIds);
}
