package org.johoeunsae.exchangediary.diary.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.block.domain.QBlock;
import org.johoeunsae.exchangediary.diary.domain.QDiary;
import org.johoeunsae.exchangediary.diary.domain.QRegistration;
import org.johoeunsae.exchangediary.dto.DiaryMemberDto;
import org.johoeunsae.exchangediary.follow.domain.QFollow;
import org.johoeunsae.exchangediary.member.domain.QMember;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RegistrationRepositoryCustomImpl implements RegistrationRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	private final QRegistration registration = QRegistration.registration;
	private final QMember member = QMember.member;
	private final QFollow follow = QFollow.follow;
	private final QBlock block = QBlock.block;
	private final QDiary diary = QDiary.diary;

	public List<DiaryMemberDto> getDiaryMembers(Long diaryId, Long memberId) {
		List<DiaryMemberDto> result = jpaQueryFactory
				.select(Projections.constructor(
						DiaryMemberDto.class,
						member,
						member.id.eq(diary.masterMember.id).as("isMaster"),
						block.id.isNotNull().as("isBlocked"),
						follow.id.isNotNull().as("isFollowing")
					)
				)
				.from(registration)
				.join(registration.member, member)
				.leftJoin(follow).on(follow.to.eq(member).and(follow.from.id.eq(memberId)))
				.leftJoin(block).on(block.to.eq(member).and(block.from.id.eq(memberId)))
				.join(registration.diary, diary)
				.where(registration.diary.id.eq(diaryId))
				.fetch();

				return result;
		}

}
