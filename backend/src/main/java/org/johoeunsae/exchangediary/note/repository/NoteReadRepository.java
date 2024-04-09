package org.johoeunsae.exchangediary.note.repository;

import java.util.Optional;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.note.domain.NoteRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteReadRepository extends JpaRepository<NoteRead, NoteMemberCompositeKey> {

	@Query("SELECT nr " +
			"FROM NoteRead nr " +
			"WHERE nr.id.memberId = :memberId")
	List<NoteRead> findAllByMemberId(@Param("memberId") Long memberId);

	@Query("SELECT nr " +
			"FROM NoteRead nr " +
			"WHERE nr.id = :id")
	Optional<NoteRead> findNoteReadById(NoteMemberCompositeKey id);
}
