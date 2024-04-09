package org.johoeunsae.exchangediary.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.johoeunsae.exchangediary.note.domain.VisibleScope.PUBLIC;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.dto.MemberNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MyNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NoteQueryServiceTest {

	@Autowired
	private NoteQueryService noteQueryService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@DisplayName("내 일기 미리보기들을 조회할 수 있다.")
	@Test
	void getMyNotePreviews() {
		LocalDateTime now = LocalDateTime.now();
		Member me = memberRepository.save(stubMember("닉네임", "이메일", now));
		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", me, now));
		List<Note> notes = noteRepository.saveAll(
				List.of(
						stubNote("제목", "내용1", now, diary, me),
						stubNote("제목", "내용2", now, diary, me),
						stubNote("제목", "내용3", now, diary, me),
						stubNote("제목", "내용4", now, diary, me))
		);
		Registration registration = registrationRepository.save(
						Registration.of(me, diary, now)
		);
		em.flush();
		em.clear();

		//when
		MyNotePreviewPaginationDto result = noteQueryService.getMyNotePreviewsByMemberId(
				me.getId(), PageRequest.of(0, 10));

		//then
		assertThat(result.getResult()).hasSize(4)
				.extracting("preview")
				.containsExactlyInAnyOrder("내용1", "내용2", "내용3", "내용4");
	}

	@DisplayName("한 사용자를 기준으로 다른 사용자의 일기 미리보기들을 조회할 수 있다.")
	@Test
	void getMemberNotePreviews() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member target = memberRepository.save(stubMember("sanan", "sanan@email.com", now));
		Member loginUser = memberRepository.save(stubMember("jpark2", "jpark2@email.com", now));
		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", target, now));
		List<Registration> registrations = registrationRepository.saveAll(
				List.of(
						Registration.of(loginUser, diary, now),
						Registration.of(target, diary, now)
				)
		);
		List<Note> notes = noteRepository.saveAll(
				List.of(
						stubNote("제목", "내용1", now, diary, target),
						stubNote("제목", "내용2", now, diary, target),
						stubNote("제목", "내용3", now, diary, target),
						stubNote("제목", "내용4", now, diary, target))
		);

		PageImpl<Note> page = new PageImpl<>(notes, PageRequest.of(0, 10), notes.size());
		em.flush();
		em.clear();

		//when
		MemberNotePreviewPaginationDto result = noteQueryService.getMemberNotePreviews(
				loginUser.getId(), target.getId(), page.getPageable());

		//then
		assertThat(result.getResult()).hasSize(4)
				.extracting("preview")
				.containsExactlyInAnyOrder("내용1", "내용2", "내용3", "내용4");
	}

	@DisplayName("한 사용자를 기준으로 다른 사용자의 특정 일기의 상세 내용과 북마크, 좋아요 여부를 조회할 수 있다.")
	@Test
	void getNoteView() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member member1 = memberRepository.save(stubMember("sanan", "sanan@email.com", now));
		Member member2 = memberRepository.save(stubMember("nanas", "nanas@email.com", now));
		Diary diary = diaryRepository.save(stubDiary("일기장 제목", "그룹 이름", member2, now));
		Note note = noteRepository.save(stubNote("제목", "내용1", now, diary, member1));
		Like like = likeRepository.save(Like.of(member2, note, LocalDateTime.now()));
		Bookmark bookmark = bookmarkRepository.save(
				Bookmark.of(member2, note, LocalDateTime.now()));
		em.flush();
		em.clear();

		//when
		NoteViewDto result = noteQueryService.getNoteView(member2.getId(), note.getId());

		//then
		assertThat(result.getNoteId()).isEqualTo(note.getId());
		assertThat(result.getTitle()).isEqualTo(note.getTitle());
		assertThat(result.getContent()).isEqualTo(note.getContent());
		assertThat(result.getAuthor().getNickname()).isEqualTo(note.getMember().getNickname());
		assertThat(result.getLikeCount()).isEqualTo(1);
		assertThat(result.isLiked()).isTrue();
		assertThat(result.isBookmarked()).isTrue();
	}

	private Member stubMember(String nickname, String email, LocalDateTime now) {
		return Member.of(MemberFeatures.of(email, nickname), MemberRole.USER, now);
	}

	private Diary stubDiary(String title, String groupName, Member member, LocalDateTime now) {
		return Diary.of(member, now, title, groupName, CoverType.COLOR);
	}

	private Note stubNote(String title, String content, LocalDateTime now, Diary diary,
			Member member) {
		return Note.of(member, diary.getId(), now, Board.of(title, content), PUBLIC);
	}
}