package org.johoeunsae.exchangediary.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.block.domain.QBlock;
import org.johoeunsae.exchangediary.dto.MemberRelationDto;
import org.johoeunsae.exchangediary.follow.domain.QFollow;
import org.johoeunsae.exchangediary.member.domain.QMember;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;
	private final QMember member = QMember.member;
	private final QBlock block = QBlock.block;
	private final QFollow follow = QFollow.follow;

	public MemberRelationDto getMemberRelationDto(Long loginMemberId, Long memberId) {
		return jpaQueryFactory
				.select(Projections.constructor(MemberRelationDto.class,
						member,
						block.id.isNotNull().as("isBlocked"),
						follow.id.isNotNull().as("isFollowing")
				))
				.from(member)
				.leftJoin(block)
				.on(block.to.id.eq(memberId).and(block.from.id.eq(loginMemberId)))
				.leftJoin(follow)
				.on(follow.to.id.eq(memberId).and(follow.from.id.eq(loginMemberId)))
				.where(member.id.eq(memberId))
				.fetchOne();
	}
}
