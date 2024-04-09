package org.johoeunsae.exchangediary.note.domain;

import org.johoeunsae.exchangediary.cloud.aws.domain.AwsS3Manager;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.dto.NoteImageCreateDto;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.repository.NoteImageRepository;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.johoeunsae.exchangediary.note.domain.VisibleScope.PUBLIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class NoteImageManagerTest {

	private final AwsS3Manager awsS3Manager = mock(AwsS3Manager.class);
	private final ImageService imageService = mock(ImageService.class);

	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

	@Autowired
	private EntityManager em;
	@Autowired
	private NoteImageRepository noteImageRepository;
	@Autowired
	private NoteImageManager noteImageManager;
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@Value("${spring.images.path.note}")
	private String NOTE_IMAGE_DIR;

	private String noteImageUrl;


	@BeforeEach
	void setUp() {
		noteImageManager = new NoteImageManager(noteImageRepository, imageService, eventPublisher);
		noteImageUrl = NOTE_IMAGE_DIR + "random-string/images.jpg";
	}

	@DisplayName("노트의 이미지를 지울 수 있다.")
	@Disabled("데이터 분리 이후에 다시 작성")
	@Test
	void test() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member member = memberRepository.save(stubMember("닉네임", "이메일", now));

		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", member, now));
		Note note = stubNote("제목", "내용", now, diary, member);
		note = noteRepository.save(note);
		List<NoteImage> noteImages1 = noteImageRepository.saveAll(List.of(
				NoteImage.of(note, 0, "note-images/썸네일 이미지 Url"),
				NoteImage.of(note, 1, "note-images/이미지 Url1"),
				NoteImage.of(note, 2, "note-images/이미지 Url2")));
		em.flush();
		em.clear();
		note = noteRepository.findById(1L).get();

		//when
		assertThat(note.getNoteImages()).hasSize(3);
		noteImageManager.deleteAllImages(note);
		em.flush();
		em.clear();

		//then
		note = noteRepository.findById(1L).get();
		assertThat(note.getNoteImages()).hasSize(0);
		then(awsS3Manager).should(times(3)).delete(any());
	}

	@DisplayName("특정 일기에 이미지들을 추가할 수 있다.")
	@Disabled("데이터 분리 이후에 다시 작성")
	@Test
	void addImagesToNote() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member member = memberRepository.save(stubMember("닉네임", "이메일", now));
		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", member, now));
		Note note = stubNote("제목", "내용", now, diary, member);
		note = noteRepository.save(note);
		List<NoteImageCreateDto> noteImages = List.of(
				new NoteImageCreateDto(0, noteImageUrl), // TODO: null 막기
				new NoteImageCreateDto(1, noteImageUrl),
				new NoteImageCreateDto(2, noteImageUrl));
		em.flush();
		em.clear();

		//when
		noteImageManager.addImagesToNote(note, noteImages);
		em.flush();
		em.clear();

		//then
		note = noteRepository.findById(1L).get();
		assertThat(note.getNoteImages()).hasSize(3);
		then(imageService).should(times(3)).validImageUrl(any(), any());
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