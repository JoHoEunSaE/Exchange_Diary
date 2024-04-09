package org.johoeunsae.exchangediary.bookmark.repository;

import java.util.Optional;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.dto.NoteRelatedInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {
	Optional<Bookmark> getBookmarkByCompositeKey(Long memberId, Long noteId);
	Page<NoteRelatedInfoDto> getBookmarkListByMemberId(Long loginMemberId, Long memberId, Pageable pageable);
}
