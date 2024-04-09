package org.johoeunsae.exchangediary.report.repository;

import java.util.List;
import java.util.Optional;
import org.johoeunsae.exchangediary.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

	@Query("SELECT r FROM Report r WHERE r.from.id = :memberFromId AND r.to.id = :memberToId")
	Optional<Report> findByMemberFromIdAndMemberToId(Long memberFromId, Long memberToId);

	@Query("SELECT COUNT(r) FROM Report r WHERE r.to.id = :memberToId")
	int countByMemberToId(Long memberToId);

	@Query("SELECT COUNT(r) FROM Report r WHERE r.note.id = :noteId")
	int countByNoteId(Long noteId);

	@Query("SELECT r FROM Report r WHERE r.to.id = :memberToId")
	List<Report> findByMemberToId(Long memberToId);
}
