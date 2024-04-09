package org.johoeunsae.exchangediary.like.repository;

import java.util.Optional;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepositoryCustom {
	Optional<Like> findByCompositeKey(Long memberId, Long noteId);
}
