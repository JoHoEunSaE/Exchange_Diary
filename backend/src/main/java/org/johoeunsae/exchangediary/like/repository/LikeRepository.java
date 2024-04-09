package org.johoeunsae.exchangediary.like.repository;

import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, NoteMemberCompositeKey> {

	@Query("SELECT l " +
			"FROM Like l " +
			"WHERE l.member.id = :memberId")
	List<Like> findByMemberId(@Param("memberId") Long memberId);

	@Query("SELECT COUNT(l) " +
			"FROM Like l " +
			"WHERE l.note.id = :noteId")
	Integer countByNoteId(@Param("noteId") Long noteId);

	@Query("SELECT COUNT(l) > 0 " +
			"FROM Like l " +
			"WHERE l.member.id = :memberId " +
			"AND l.note.id = :noteId")
	boolean existsByMemberAndNoteId(@Param("memberId") Long memberId, @Param("noteId") Long noteId);

	void deleteAllByNoteId(Long noteId);
}
