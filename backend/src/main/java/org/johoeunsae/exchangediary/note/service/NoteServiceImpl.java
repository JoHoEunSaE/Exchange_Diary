package org.johoeunsae.exchangediary.note.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.dto.NoteImageCreateDto;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImageManager;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.UNAUTHENTICATED_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.*;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus.NOT_FOUND_NOTE;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

	private final MemberRepository memberRepository;
	private final NoteRepository noteRepository;
	private final NoteImageManager noteImageManager;
	private final LikeRepository likeRepository;
	private final BookmarkRepository bookmarkRepository;
	private final DiaryRepository diaryRepository;

	@Override
	public void updateNote(Long userId, Long noteId, String title, String content) {
		log.debug("Called updateNote: userId={}, noteId={}, title={}, content={}", userId, noteId,
				title, content);
		Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new DomainException(NOT_FOUND_MEMBER));
		Note note = noteRepository.findById(noteId)
				.orElseThrow(() -> new DomainException(NOT_FOUND_NOTE));
		if (!note.isOwnedBy(member)) {
			throw UNAUTHENTICATED_MEMBER.toServiceException();
		}
		note.writeTitle(title);
		note.writeContent(content);
	}

	@Override
	public void deleteNote(Long noteId, Long memberId, LocalDateTime now) {
		log.debug("Called deleteNote: noteId={}, memberId={}", noteId, memberId);
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new DomainException(NOT_FOUND_MEMBER));
		Note note = noteRepository.findById(noteId)
				.orElseThrow(() -> new DomainException(NOT_FOUND_NOTE));
		if (!note.isOwnedBy(member)) {
			throw UNAUTHENTICATED_MEMBER.toServiceException();
		}
		bookmarkRepository.deleteAllByNoteId(noteId);
		likeRepository.deleteAllByNoteId(noteId);
		noteImageManager.deleteAllImages(note);
		noteRepository.softDelete(note.getId(), now);
	}

	@Override
	public void deleteNoteImages(Long noteId, Long userId, List<Integer> imageIndexes) {
		log.debug("Called deleteNoteImages: noteId={}, userId={}, imageIndexes={}", noteId, userId,
				imageIndexes);
		Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new DomainException(NOT_FOUND_MEMBER));
		Note note = noteRepository.findById(noteId)
				.orElseThrow(() -> new DomainException(NOT_FOUND_NOTE));
		if (!note.isOwnedBy(member)) {
			throw UNAUTHENTICATED_MEMBER.toServiceException();
		}
		noteImageManager.deleteImages(note, imageIndexes);
	}

	@Override
	public void tearOffNotesFromDiary(Long diaryId) {
		log.debug("Called tearOffNotesFromDiary: diaryId={}", diaryId);
		noteRepository.updateDiaryIdByDiaryId(diaryId, Diary.DEFAULT_DIARY_ID);
	}

	@Override
	public void tearOffNoteFromDiary(Long memberId, Long diaryId, Long noteId) {
		log.debug("Called tearOffNoteFromDiary: memberId={}, diaryId={}, noteId={}", memberId,
				diaryId, noteId);

		Member member = memberRepository.findById(memberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		Note note = noteRepository.findById(noteId)
				.orElseThrow(NOT_FOUND_NOTE::toServiceException);
		if (!note.isOwnedBy(member)) {
			throw NO_PERMISSION_TEAR_OFF_NOTE.toServiceException();
		}
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);
		if (!diary.isBelongToDiary(note)) {
			throw NOTE_NOT_BELONG_TO_DIARY.toServiceException();
		}
		noteRepository.updateDiaryIdByNoteId(noteId, Diary.DEFAULT_DIARY_ID);
	}

	@Override
	public void tearOffMemberNotesFromDiary(Long memberId, Long diaryId) {
		log.debug("Called tearOffMemberNotesFromDiary: memberId={}, diaryId={}", memberId,
				diaryId);

		noteRepository.updateDiaryIdByMemberId(memberId, diaryId, Diary.DEFAULT_DIARY_ID);
	}

	/**
	 * 현재 사용자의 Id를 기준으로 일기를 생성합니다.
	 *
	 * @param loginUserId  현재 사용자의 Id
	 * @param title        일기 제목
	 * @param content      일기 내용
	 * @param visibleScope 일기 공개 범위
	 * @param imageData    일기에 포함될 이미지 이진 데이터와 인덱스의 컬렉션
	 * @param now          일기 생성 시각
	 * @param diary        일기가 속할 일기장
	 * @see UserSessionDto
	 * @see LoginUserInfo
	 */
	@Override
	public Note createNoteToDiary(Long loginUserId, String title, String content,
			VisibleScope visibleScope,
			List<NoteImageCreateDto> imageData, LocalDateTime now, Diary diary) {
		log.debug(
				"Called createNoteToDiary: loginUserId={}, title={}, content={}, visibleScope={}, imageData={}, now={}, diary={}",
				loginUserId, title, content, visibleScope, imageData, now, diary);
		/* OPTIONAL: 사용자의 일기 생성에 대한 제약 조건 */
		Member member = memberRepository.findById(loginUserId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		Note note = noteRepository.save(
				Note.of(member, diary.getId(), now, Board.of(title, content), visibleScope));
		noteImageManager.addImagesToNote(note, imageData);
		return noteRepository.save(note);
	}
}
