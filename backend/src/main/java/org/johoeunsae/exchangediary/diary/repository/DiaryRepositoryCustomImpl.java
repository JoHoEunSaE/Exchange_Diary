package org.johoeunsae.exchangediary.diary.repository;

import static org.johoeunsae.exchangediary.diary.domain.QRegistration.registration;
import static org.johoeunsae.exchangediary.member.domain.QMember.member;
import static org.johoeunsae.exchangediary.note.domain.QNote.note;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.block.domain.QBlock;
import org.johoeunsae.exchangediary.diary.domain.CoverImage;
import org.johoeunsae.exchangediary.diary.domain.QCoverImage;
import org.johoeunsae.exchangediary.diary.domain.QDiary;
import org.johoeunsae.exchangediary.dto.DiaryNoteMemberDto;
import org.johoeunsae.exchangediary.dto.DiaryRecentNoteDto;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QDiary diary = QDiary.diary;
	private final QBlock block = QBlock.block;
	private final QCoverImage coverImage = QCoverImage.coverImage;

	@Override
	public void deleteCoverImage(CoverImage coverImage) {
		jpaQueryFactory.delete(this.coverImage)
				.where(this.coverImage.id.eq(coverImage.getId()))
				.execute();
	}

	@Override
	public List<DiaryRecentNoteDto> getRecentDiaryAndNoteWithAuthor(Long loginMemberId){
		List<DiaryRecentNoteDto> result = jpaQueryFactory
				.select(Projections.constructor(
								DiaryRecentNoteDto.class,
								member.id.as("memberId"),
								member.nickname.as("nickname"),
								member.profileImageUrl.as("profileImageUrl"),
								diary.id.as("diaryId"),
								diary.title.as("diaryTitle"),
								diary.groupName.as("groupName"),
								note
						)
				)
				.from(note)
				.join(diary).on(note.diaryId.eq(diary.id))
				.join(member).on(note.member.id.eq(member.id))
				.where(note.createdAt.eq(
						jpaQueryFactory.select(note.createdAt.max())
								.from(note)
								.where(note.diaryId.eq(diary.id))
				))
				.where(diary.id.in(
						jpaQueryFactory
								.select(registration.diary.id)
								.from(registration)
								.where(registration.member.id.eq(loginMemberId))
				))
				.groupBy(note.diaryId)
				.fetch();
		return result;
	}

	// getRecentDiaryAndNoteWithAuthor와 동일한 쿼리. 매핑되는 class만 다르다.
	@Override
	public List<DiaryNoteMemberDto> getDiaryNoteMembers(Long loginMemberId) {
		BooleanBuilder builder = getNoteVisibilityBuilder();
		return jpaQueryFactory
				.select(Projections.constructor(
								DiaryNoteMemberDto.class,
								diary.id.as("diaryId"),
								diary.title.as("diaryTitle"),
								diary.groupName.as("groupName"),
								note,
								member,
								block.id.isNotNull().as("isBlocked")
						)
				)
				.from(note)
				.join(diary).on(note.diaryId.eq(diary.id))
				.join(member).on(note.member.id.eq(member.id))
				.leftJoin(block)
				.on(block.from.id.eq(loginMemberId).and(block.to.id.eq(note.member.id)))
				.where(note.createdAt.eq(
						jpaQueryFactory.select(note.createdAt.max())
								.from(note)
								.where(note.diaryId.eq(diary.id))
								.where(builder)
				))
				.where(diary.id.in(
						jpaQueryFactory
								.select(registration.diary.id)
								.from(registration)
								.where(registration.member.id.eq(loginMemberId))
				))
				.groupBy(note.diaryId)
				.fetch();
	}

	private BooleanBuilder getNoteVisibilityBuilder() {
		BooleanBuilder builder = new BooleanBuilder();
		// 블라인드 된 일기들을 제외. 추후 새로운 visibleScope가 추가되면 수정해야 함.
		builder.and(note.visibleScope.in(VisibleScope.PUBLIC, VisibleScope.PRIVATE));
		// 삭제된 일기들을 제외
		builder.and(note.deletedAt.isNull());
		return builder;
	}
}
