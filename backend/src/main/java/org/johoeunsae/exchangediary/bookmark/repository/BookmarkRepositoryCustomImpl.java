package org.johoeunsae.exchangediary.bookmark.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.block.domain.QBlock;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.bookmark.domain.QBookmark;
import org.johoeunsae.exchangediary.diary.domain.QDiary;
import org.johoeunsae.exchangediary.dto.NoteRelatedInfoDto;
import org.johoeunsae.exchangediary.member.domain.QMember;
import org.johoeunsae.exchangediary.note.domain.QNote;
import org.johoeunsae.exchangediary.note.domain.QNoteRead;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;
	private final QBookmark bookmark = QBookmark.bookmark;
	private final QNote note = QNote.note;
	private final QDiary diary = QDiary.diary;
	private final QNoteRead noteRead = QNoteRead.noteRead;
	private final QMember member = QMember.member;
	private final QBlock block = QBlock.block;

	@Override
	public Optional<Bookmark> getBookmarkByCompositeKey(Long memberId, Long noteId) {
		return Optional.ofNullable(
				jpaQueryFactory
						.selectFrom(bookmark)
						.where(bookmark.id.memberId.eq(memberId), bookmark.id.noteId.eq(noteId))
						.fetchOne()
		);
	}

	// 일기 읽음 여부 (hasRead)에 대한 최적화?
	@Override
	public Page<NoteRelatedInfoDto> getBookmarkListByMemberId(Long loginMemberId, Long memberId, Pageable pageable) {
		// where 조건
		// memberId가 북마크
		// 로그인한 유저가 노트를 볼 수 있는 경우
		BooleanExpression where = bookmark.member.id.eq(memberId)
				.and(isNoteVisible(loginMemberId));

		// 일기를 뜯어내면 diaryId가 0이 되는데, 0이라는 아이디를 가진 다이어리는 실제로 존재하지 않으므로 leftJoin
		// 다이어리가 없는 경우 groupName과 title은 빈 문자열로 반환
		List<NoteRelatedInfoDto> result = jpaQueryFactory
				.select(Projections.constructor(
						NoteRelatedInfoDto.class,
						note,
						note.member,
						note.diaryId,
						Expressions.stringTemplate("COALESCE({0}, '')", diary.title),
						Expressions.stringTemplate("COALESCE({0}, '')", diary.groupName),
						noteRead.id.isNotNull().as("hasRead"),
						block.id.isNotNull().as("isBlocked")
				))
				.from(bookmark)
				.join(bookmark.note, note)
				.join(note.member, member)
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.leftJoin(noteRead)
				.on(noteRead.note.id.eq(note.id).and(noteRead.member.id.eq(loginMemberId)))
				.leftJoin(block)
				.on(block.from.id.eq(loginMemberId).and(block.to.id.eq(note.member.id)))
				.where(where)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long totalLength = jpaQueryFactory
				.select(bookmark.count())
				.from(bookmark)
				.join(bookmark.note, note)
				.join(note.member, member)
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.fetchOne();

		return new PageImpl<>(result, pageable, totalLength);
	}

	// 로그인 유저가 노트를 볼 수 있는 경우
	// 1. 본인이 쓴 일기
	// 2. visibleScope == PUBLIC
	// 3. visibleScope == PRIVATE && 본인이 속한 일기장에 쓰여진 일기
	// 4. deletedAt이 null인 경우
	private BooleanExpression isNoteVisible(Long loginMemberId) {
		return note.deletedAt.isNull().and(
				note.visibleScope.eq(VisibleScope.PUBLIC)
						.or(note.member.id.eq(loginMemberId))
						.or(note.visibleScope.eq(VisibleScope.PRIVATE)
								.and(diary.registrations.any().member.id.eq(loginMemberId))));
	}
}