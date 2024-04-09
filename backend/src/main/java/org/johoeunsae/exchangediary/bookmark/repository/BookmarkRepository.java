package org.johoeunsae.exchangediary.bookmark.repository;

import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, NoteMemberCompositeKey>, BookmarkRepositoryCustom {

	@Query("SELECT b " +
			"FROM Bookmark b " +
			"WHERE b.member.id = :memberId")
	List<Bookmark> findByMemberId(@Param("memberId") Long memberId);

	@Query("SELECT COUNT(b) > 0 " +
			"FROM Bookmark b " +
			"WHERE b.member.id = :memberId " +
			"AND b.note.id = :noteId")
	boolean existsByMemberAndNoteId(@Param("memberId") Long memberId, @Param("noteId") Long noteId);

	void deleteAllByNoteId(Long noteId);
}
