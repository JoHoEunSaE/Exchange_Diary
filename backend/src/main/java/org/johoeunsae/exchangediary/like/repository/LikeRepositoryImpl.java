package org.johoeunsae.exchangediary.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.like.domain.QLike;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;
	private final QLike like = QLike.like;

	public Optional<Like> findByCompositeKey(Long memberId, Long noteId) {
		return jpaQueryFactory
				.selectFrom(like)
				.where(like.id.memberId.eq(memberId), like.id.noteId.eq(noteId))
				.fetch()
				.stream()
				.findFirst();
	}
}
