package org.johoeunsae.exchangediary.note.repository;

import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteImageRepository extends JpaRepository<NoteImage, Long> {
}
