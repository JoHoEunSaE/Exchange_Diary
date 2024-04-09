package org.johoeunsae.exchangediary.note.repository;

import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>, NoteRepositoryCustom {

	@Query("SELECT n " +
			"FROM Note n " +
			"WHERE n.member.id = :memberId AND n.deletedAt IS NULL")
	Page<Note> findAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT n " +
			"FROM Note n " +
			"WHERE n.member.id = :memberId and n.visibleScope = :visibleScope AND n.deletedAt IS NULL")
	Page<Note> findAllByMemberIdAndVisibleScope(@Param("memberId") Long memberId,
	                                            @Param("visibleScope") VisibleScope visibleScope, Pageable pageable);

	@Query("SELECT n " +
			"FROM Note n " +
			"WHERE n.diaryId = :diaryId AND n.deletedAt IS NULL")
	Page<Note> findAllByDiaryId(@Param("diaryId") Long diaryId, Pageable pageable);

	@Query("SELECT n " +
			"FROM Note n " +
			"WHERE n.id = :noteId AND n.deletedAt IS NULL")
	Optional<Note> findById(@Param("noteId") Long noteId);

	@Modifying
	@Query("UPDATE Note n " +
			"SET n.deletedAt = :deletedAt " +
			"WHERE n.id = :noteId")
	void softDelete(@Param("noteId") Long noteId, @Param("deletedAt") LocalDateTime deletedAt);
}
