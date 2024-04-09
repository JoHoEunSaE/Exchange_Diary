package org.johoeunsae.exchangediary.bookmark.service;

import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.UNAUTHENTICATED_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.BookmarkExceptionStatus.NOT_FOUND_BOOKMARK;
import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NON_EXIST_DIARY;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus.NOT_FOUND_NOTE;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepositoryCustom;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
	private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;
	private final DiaryRepository diaryRepository;

    public void createBookmark(Long memberId, Long noteId) {
	    Member member = memberRepository.findById(memberId).orElseThrow(NOT_FOUND_MEMBER::toServiceException);
        Note note = noteRepository.findById(noteId).orElseThrow(NOT_FOUND_NOTE::toServiceException);

	    // 중복 Like 방지
	    if (bookmarkRepository.getBookmarkByCompositeKey(memberId, noteId).isPresent()) {
		    return ;
	    }

	    if (note.getVisibleScope() == VisibleScope.PRIVATE) {
		    Diary diary = diaryRepository.findById(note.getDiaryId()).orElseThrow(NON_EXIST_DIARY::toServiceException);
		    if (!diary.isDiaryMember(member)) {
			    throw UNAUTHENTICATED_MEMBER.toServiceException();
		    }
	    }

        Bookmark bookmark = Bookmark.of(member, note, LocalDateTime.now());
        bookmarkRepository.save(bookmark);
    }

	public void deleteBookmark(Long memberId, Long noteId) {
		Bookmark bookmark = bookmarkRepository.getBookmarkByCompositeKey(memberId, noteId).orElseThrow(NOT_FOUND_BOOKMARK::toServiceException);
		bookmarkRepository.delete(bookmark);
	}
}
