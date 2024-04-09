package org.johoeunsae.exchangediary.note.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.repository.NoteImageRepository;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.johoeunsae.exchangediary.note.domain.VisibleScope.PUBLIC;

/**
 * 임시로 작성한 테스트입니다.
 */
@DataJpaTest
@ActiveProfiles("test")
class NoteTest {

	@PersistenceContext
	EntityManager em;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private NoteImageRepository noteImageRepository;

	@Autowired
	private NoteRepository noteRepository;

	@DisplayName("썸네일 이미지의 Url을 조회할 수 있다.")
	@Test
	void getThumbnailUrl() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member member = memberRepository.save(stubMember("닉네임", "이메일", now));
		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", member, now));
		Note note = stubNote("제목", "내용", now, diary, member);
		noteRepository.save(note);
		noteImageRepository.saveAll(List.of(
				NoteImage.of(note, 0, "썸네일 이미지 Url"),
				NoteImage.of(note, 1, "이미지 Url1"),
				NoteImage.of(note, 2, "이미지 Url2")));

		//when
		em.clear();
		Note result = noteRepository.findAllByMemberId(1L, PageRequest.of(0, 10)).getContent()
				.get(0);

		//then
		assertThat(result.getThumbnailUrl()).isEqualTo("썸네일 이미지 Url");
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

	@TestConfiguration
	static class NoteTestConfiguration {

		@Autowired
		EntityManager em;

		@Bean
		JPAQueryFactory jpaQueryFactory() {
			return new JPAQueryFactory(em);
		}
	}

}