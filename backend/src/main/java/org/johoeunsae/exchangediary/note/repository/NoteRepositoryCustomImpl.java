package org.johoeunsae.exchangediary.note.repository;

import static org.johoeunsae.exchangediary.utils.querydsl.QueryDSLUtil.getOrderSpecifiers;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.diary.domain.QDiary;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.QNote;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoteRepositoryCustomImpl implements NoteRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QNote note = QNote.note;
	private final QDiary diary = QDiary.diary;


	// ---------------------------------- < 노트 조회 조건 > -------------------------------------
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

	// 블라인드 일기인 경우 로그인 한 유저의 노트라면 보이도록 함.
	private BooleanExpression isBlindNoteVisible(Long loginMemberId) {
		return note.visibleScope.in(VisibleScope.PUBLIC_BLIND, VisibleScope.PRIVATE_BLIND)
				.and(note.member.id.eq(loginMemberId));
	}

	// -----------------------------------------------------------------------------------------

	private BooleanBuilder getNoteVisibilityBuilder2(Long loginMemberId) {
		return new BooleanBuilder()
				.and(isNoteVisible(loginMemberId).or(isBlindNoteVisible(loginMemberId)));
	}

	@Override
	public String findGroupNameByNoteId(Long noteId) {
		String groupName = jpaQueryFactory
				.select(diary.groupName)
				.from(note)
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.where(note.id.eq(noteId))
				.fetchFirst();
		return groupName;
	}

	@Override
	public void updateDiaryIdByDiaryId(Long beforeDiaryId, Long afterDiaryId) {
		jpaQueryFactory.update(note)
				.set(note.diaryId, afterDiaryId)
				.where(note.diaryId.eq(beforeDiaryId))
				.execute();
	}

	@Override
	public void updateDiaryIdByNoteId(Long noteId, Long afterDiaryId) {
		jpaQueryFactory.update(note)
				.set(note.diaryId, afterDiaryId)
				.where(note.id.eq(noteId))
				.execute();
	}

	@Override
	public Page<Note> findPublicNotes(Long loginUserId, Pageable pageable) {
		List<Note> result = jpaQueryFactory
				.select(note)
				.from(note)
				.leftJoin(note.member).fetchJoin()
				.orderBy(note.createdAt.desc())
				.where(note.visibleScope.eq(VisibleScope.PUBLIC))
				.where(note.deletedAt.isNull())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long count = jpaQueryFactory
				.select(note.count())
				.from(note)
				.where(note.visibleScope.eq(VisibleScope.PUBLIC))
				.where(note.deletedAt.isNull())
				.fetchFirst();
		return new PageImpl<>(result, pageable, count);
	}

	@Override
	public void updateDiaryIdByMemberId(Long memberId, Long beforeDiaryId, Long afterDiaryId) {
		jpaQueryFactory.update(note)
				.set(note.diaryId, afterDiaryId)
				.where(note.diaryId.eq(beforeDiaryId))
				.where(note.member.id.eq(memberId))
				.execute();
	}

	@Override
	public Optional<Note> findPreviousNoteByPresentNote(Long loginUserId, Note presentNote,
			Long diaryId) {
		BooleanExpression where = note.diaryId.eq(diaryId)
				.and(getNoteVisibilityBuilder2(loginUserId));

		Note previousNote = jpaQueryFactory
				.select(note)
				.from(note)
				.leftJoin(note.member).fetchJoin()
				.join(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.where(note.createdAt.lt(presentNote.getCreatedAt()))
				.orderBy(note.createdAt.desc())
				.fetchFirst();
		return Optional.ofNullable(previousNote);
	}

	@Override
	public Optional<Note> findNextNoteByPresentNote(Long loginUserId, Note presentNote,
			Long diaryId) {
		BooleanExpression where = note.diaryId.eq(diaryId)
				.and(getNoteVisibilityBuilder2(loginUserId));

		Note nextNote = jpaQueryFactory
				.select(note)
				.from(note)
				.leftJoin(note.member).fetchJoin()
				.join(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.where(note.createdAt.gt(presentNote.getCreatedAt()))
				.orderBy(note.createdAt.asc())
				.fetchFirst();

		return Optional.ofNullable(nextNote);
	}

	@Override
	public Page<Note> findVisibleNotesByDiaryId(Long loginUserId, Long diaryId, Pageable pageable) {
		// where 조건
		BooleanExpression where = note.diaryId.eq(diaryId)
				.and(getNoteVisibilityBuilder2(loginUserId));
		// Pageable에서 정렬 정보 가져오기

		List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(
				pageable, Note.class);

		List<Note> result = jpaQueryFactory
				.select(note)
				.from(note)
				.leftJoin(note.member).fetchJoin()
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // 정렬 적용
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long totalLength = jpaQueryFactory
				.select(note.count())
				.from(note)
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.fetchFirst();

		return new PageImpl<>(result, pageable, totalLength);

	}

	@Override
	public Page<Note> findVisibleNotesByMemberId(Long loginUserId, Long memberId,
			Pageable pageable) {
		// where 조건
		BooleanExpression where = note.member.id.eq(memberId)
				.and(getNoteVisibilityBuilder2(loginUserId));

		List<Note> result = jpaQueryFactory
				.select(note)
				.from(note)
				.leftJoin(note.member).fetchJoin()
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long totalLength = jpaQueryFactory
				.select(note.count())
				.from(note)
				.leftJoin(diary)
				.on(note.diaryId.eq(diary.id))
				.where(where)
				.fetchFirst();

		return new PageImpl<>(result, pageable, totalLength);

	}

	@Override
	public Optional<Note> findVisibleNoteByNoteId(Long loginUserId, Long noteId) {
		BooleanExpression where = note.id.eq(noteId).and(getNoteVisibilityBuilder1(loginUserId));

		Note result = jpaQueryFactory
				.selectFrom(note)
				.where(where)
				.fetchOne();

		return Optional.ofNullable(result);
	}

	private BooleanBuilder getNoteVisibilityBuilder1(Long loginMemberId) {
		BooleanBuilder builder = new BooleanBuilder();
		// 블라인드 된 일기들을 제외. 추후 새로운 visibleScope가 추가되면 수정해야 함.
		// 블라인드 일기인 경우 로그인 한 유저의 노트라면 보이도록 함.
		builder.and(note.visibleScope.in(VisibleScope.PUBLIC, VisibleScope.PRIVATE)
				.or(note.member.id.eq(loginMemberId)));
		// 삭제된 일기들을 제외
		builder.and(note.deletedAt.isNull());
		return builder;
	}
}
