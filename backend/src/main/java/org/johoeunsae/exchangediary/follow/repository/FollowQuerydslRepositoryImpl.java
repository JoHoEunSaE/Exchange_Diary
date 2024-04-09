package org.johoeunsae.exchangediary.follow.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.follow.domain.QFollow;
import org.johoeunsae.exchangediary.follow.repository.dto.MemberPrivacy;
import org.johoeunsae.exchangediary.member.domain.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowQuerydslRepositoryImpl implements FollowQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * <code>
	 * select f.member_id, m.nickname, m.profile_image_url from follow f inner join member m on m.id
	 * = f.member_id where f.target_member_id = :memberId;
	 * </code>
	 *
	 * @param memberId
	 * @return
	 */
	@Override
	public Page<MemberPrivacy> findFollowerPrivacyList(Long memberId, Pageable pageable) {
		QMember m = QMember.member;
		QFollow f = QFollow.follow;

		List<MemberPrivacy> fetch = queryFactory
				.select(Projections
						.constructor(MemberPrivacy.class, f.from.id, f.from.nickname,
								f.from.profileImageUrl))
				.from(f)
				.innerJoin(m).on(m.id.eq(f.from.id))
				.where(f.to.id.eq(memberId))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		long totalLength = queryFactory
				.select(Projections
						.constructor(MemberPrivacy.class, f.from.id, f.from.nickname,
								f.from.profileImageUrl))
				.from(f)
				.innerJoin(m).on(m.id.eq(f.from.id))
				.where(f.to.id.eq(memberId))
				.fetch()
				.size();

		return new PageImpl<>(fetch, pageable, totalLength);
	}

	/**
	 * <code>
	 * select f.target_member_id, m.nickname, m.profile_image_url from follow f inner join member m
	 * on m.id = f.target_member_id where f.member_id = :memberId;
	 * </code>
	 *
	 * @param memberId
	 * @return
	 */
	@Override
	public Page<MemberPrivacy> findFollowingPrivacyList(Long memberId, Pageable pageable) {
		QMember m = QMember.member;
		QFollow f = QFollow.follow;

		List<MemberPrivacy> fetch = queryFactory
				.select(Projections
						.constructor(MemberPrivacy.class, f.to.id, m.nickname, m.profileImageUrl))
				.from(f)
				.innerJoin(m).on(m.id.eq(f.to.id))
				.where(f.from.id.eq(memberId))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		long totalLength = queryFactory
				.select(Projections
						.constructor(MemberPrivacy.class, f.to.id, m.nickname, m.profileImageUrl))
				.from(f)
				.innerJoin(m).on(m.id.eq(f.to.id))
				.where(f.from.id.eq(memberId))
				.fetch()
				.size();

		return new PageImpl<>(fetch, pageable, totalLength);
	}

	@Override
	public Page<MemberPreviewDto> findFollowerPrivacyListForLogin(Long loginId, Long targetId,
			Pageable pageable) {
		QMember m = QMember.member;
		QFollow f1 = new QFollow("f1");
		QFollow f2 = new QFollow("f2");

		List<MemberPreviewDto> fetch = queryFactory
				.select(Projections
						.constructor(MemberPreviewDto.class, m.id, m.nickname, m.profileImageUrl,
								f2.from.id.isNotNull()))
				.from(m)
				.innerJoin(f1).on(m.id.eq(f1.from.id).and(f1.to.id.eq(targetId)))
				.leftJoin(f2).on(f1.from.id.eq(f2.id.targetMemberId).and(f2.from.id.eq(loginId)))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		long totalLength = queryFactory
				.select(Projections
						.constructor(MemberPreviewDto.class, m.id, m.nickname, m.profileImageUrl,
								f2.from.id.isNotNull()))
				.from(m)
				.innerJoin(f1).on(m.id.eq(f1.from.id).and(f1.to.id.eq(targetId)))
				.leftJoin(f2).on(f1.from.id.eq(f2.id.targetMemberId).and(f2.from.id.eq(loginId)))
				.fetch()
				.size();

		return new PageImpl<>(fetch, pageable, totalLength);
	}

	@Override
	public Page<MemberPreviewDto> findFollowingPrivacyListForLogin(Long loginId, Long targetId,
			Pageable pageable) {
		QMember m = QMember.member;
		QFollow f1 = new QFollow("f1");
		QFollow f2 = new QFollow("f2");

		List<MemberPreviewDto> fetch = queryFactory
				.select(Projections
						.constructor(MemberPreviewDto.class, m.id, m.nickname, m.profileImageUrl,
								f2.from.id.isNotNull()))
				.from(m)
				.innerJoin(f1).on(m.id.eq(f1.to.id).and(f1.from.id.eq(targetId)))
				.leftJoin(f2).on(f1.to.id.eq(f2.id.targetMemberId).and(f2.from.id.eq(loginId)))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		long totalLength = queryFactory
				.select(Projections
						.constructor(MemberPreviewDto.class, m.id, m.nickname, m.profileImageUrl,
								f2.from.id.isNotNull()))
				.from(m)
				.innerJoin(f1).on(m.id.eq(f1.to.id).and(f1.from.id.eq(targetId)))
				.leftJoin(f2).on(f1.to.id.eq(f2.id.targetMemberId).and(f2.from.id.eq(loginId)))
				.fetch()
				.size();

		return new PageImpl<>(fetch, pageable, totalLength);
	}

	@Override
	public boolean existsTargetMemberIdByMemberId(Long fromMemberId, Long toMemberId) {
		QFollow f = QFollow.follow;
		return queryFactory
				.from(f)
				.select(f)
				.where(f.to.id.eq(toMemberId).and(f.from.id.eq(fromMemberId)))
				.fetchFirst() != null;
	}
}
