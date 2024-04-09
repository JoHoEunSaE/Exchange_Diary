package org.johoeunsae.exchangediary.like.service;

import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.UNAUTHENTICATED_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NON_EXIST_DIARY;
import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NO_PERMISSION_WRITE_NOTE;
import static org.johoeunsae.exchangediary.exception.status.LikeExceptionStatus.NOT_FOUND_LIKE;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus.NOT_FOUND_NOTE;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.like.repository.LikeRepositoryCustom;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;
    private final LikeRepository likeRepository;
	private final DiaryRepository diaryRepository;
	private final LikeRepositoryCustom likeRepositoryCustom;

    @Override
    public void createLike(Long noteId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NOT_FOUND_MEMBER::toServiceException);
        Note note = noteRepository.findById(noteId).orElseThrow(NOT_FOUND_NOTE::toServiceException);

		// 중복 Like 방지
	    if (likeRepositoryCustom.findByCompositeKey(memberId, noteId).isPresent()) {
		    return ;
	    }

	    if (note.getVisibleScope() == VisibleScope.PRIVATE) {
			Diary diary = diaryRepository.findById(note.getDiaryId()).orElseThrow(NON_EXIST_DIARY::toServiceException);
			if (!diary.isDiaryMember(member)) {
				throw UNAUTHENTICATED_MEMBER.toServiceException();
			}
		}
        Like like = Like.of(member, note, LocalDateTime.now());
        likeRepository.save(like);
    }

    @Override
	public void deleteLike(Long noteId, Long memberId) {
	    Like like = likeRepositoryCustom.findByCompositeKey(memberId, noteId).orElseThrow(NOT_FOUND_LIKE::toServiceException);
		likeRepository.delete(like);
	}
}
