package org.johoeunsae.exchangediary.note.service;

import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.dto.NoteImageCreateDto;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.johoeunsae.exchangediary.note.domain.NoteImageManager;
import org.johoeunsae.exchangediary.note.repository.NoteImageRepository;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
class NoteServiceTest {

	private NoteService noteService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private EntityManager em;

	@Mock
	private ImageService imageService = mock(ImageService.class);

	@Mock
	private ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

	private NoteImageManager imageManager;

	@Autowired
	private NoteImageRepository noteImageRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Value("${spring.images.path.note}")
	private String NOTE_IMAGE_DIR;

	private String normalImageUrl;


	@BeforeEach
	void setUp() {
		imageManager = new NoteImageManager(noteImageRepository, imageService, eventPublisher);
		noteService = new NoteServiceImpl(memberRepository, noteRepository, imageManager, likeRepository, bookmarkRepository,
				diaryRepository);
		normalImageUrl = NOTE_IMAGE_DIR + "random-string/image1.jpg";
	}

	@DisplayName("본인의 일기를 업데이트 할 수 있다.")
	@Test
	void updateNote() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member author = memberRepository.save(stubMember("sanan", "sanan@email.com", now));
		Diary diary = diaryRepository.save(stubDiary("일기장 제목", "그룹 이름", author, now));
		Note note = noteRepository.save(stubNote("제목", "내용1", now, diary, author));
		em.flush();
		em.clear();

		//when
		noteService.updateNote(author.getId(), note.getId(), "바뀐 제목", "바뀐 내용");
		em.flush();
		em.clear();
		Note updatedNote = noteRepository.findById(note.getId()).get();

		//then
		assertThat(updatedNote.getTitle()).isEqualTo("바뀐 제목");
		assertThat(updatedNote.getContent()).isEqualTo("바뀐 내용");
	}

	@DisplayName("본인이 작성한 일기를 삭제할 수 있다.")
	@Test
	void deleteNote() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member author = memberRepository.save(stubMember("sanan", "sanan@email.com", now));
		Diary diary = diaryRepository.save(stubDiary("일기장 제목", "그룹 이름", author, now));
		Note note = noteRepository.save(stubNote("제목", "내용1", now, diary, author));
		em.flush();
		em.clear();

		//when
		noteService.deleteNote(note.getId(), author.getId(), LocalDateTime.now());
		em.flush();
		em.clear();

		//then
		assertThat(noteRepository.findById(note.getId())).isEmpty();
	}

	@DisplayName("일기에 담긴 이미지들을 원하는 순서를 지정하여 삭제하고, 순서를 재정렬한다.")
	@Test
	void deleteNoteImages() {
		//given
		String NOTE_IMAGE_DIRECTORY = "note-images";
		LocalDateTime now = LocalDateTime.now();
		Member loginUser = memberRepository.save(stubMember("닉네임", "이메일", now));
		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", loginUser, now));
		Note note = noteRepository.save(stubNote("제목", "내용", now, diary, loginUser));
		List<NoteImage> images = List.of(
				NoteImage.of(note, 0, NOTE_IMAGE_DIRECTORY + "/image1"),
				NoteImage.of(note, 1, NOTE_IMAGE_DIRECTORY + "/image2"),
				NoteImage.of(note, 2, NOTE_IMAGE_DIRECTORY + "/image3"),
				NoteImage.of(note, 3, NOTE_IMAGE_DIRECTORY + "/image4"),
				NoteImage.of(note, 4, NOTE_IMAGE_DIRECTORY + "/image5"));
		List<NoteImage> noteImages = noteImageRepository.saveAll(images);
		note.addNoteImages(noteImages);
		em.flush();
		em.clear();

		//when
		noteService.deleteNoteImages(note.getId(), loginUser.getId(), List.of(0, 2, 4));
		em.flush();
		em.clear();
		List<NoteImage> result = noteImageRepository.findAll();

		//then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getIndex()).isEqualTo(0);
		assertThat(result.get(1).getIndex()).isEqualTo(1);
		assertThat(result.get(0).getImageUrl()).isEqualTo(NOTE_IMAGE_DIRECTORY + "/image2");
		assertThat(result.get(1).getImageUrl()).isEqualTo(NOTE_IMAGE_DIRECTORY + "/image4");
	}

	@DisplayName("사용자가 특정 일기장에 속하는 일기를 생성할 수 있다.")
	@Disabled("동작은 하지만 Data 분리가 되지 않아서 실패함")
	@Test
	void createNoteToDiary() {
		//given
		LocalDateTime now = LocalDateTime.now();
		Member loginUser = memberRepository.save(stubMember("닉네임", "이메일", now));
		Diary diary = diaryRepository.save(
				stubDiary("일기장 제목", "그룹 이름", loginUser, now));
		noteRepository.save(stubNote("제목", "내용", now, diary, loginUser));

		List<NoteImageCreateDto> noteImages = List.of(
				new NoteImageCreateDto(0, normalImageUrl), // TODO: null 막기
				new NoteImageCreateDto(1, normalImageUrl),
				new NoteImageCreateDto(2, normalImageUrl));

		//when
		Note createdNote = noteService.createNoteToDiary(loginUser.getId(), "제목", "내용",
				PUBLIC, noteImages,
				now, diary);
		em.flush();
		em.clear();
		createdNote = noteRepository.findById(createdNote.getId()).get();

		//then
		assertThat(createdNote.getId()).isEqualTo(2);
		assertThat(createdNote.getTitle()).isEqualTo("제목");
		assertThat(createdNote.getContent()).isEqualTo("내용");
		assertThat(createdNote.getDiaryId()).isEqualTo(1);
		assertThat(createdNote.getMember().getId()).isEqualTo(1);
		assertThat(createdNote.getNoteImages().size()).isEqualTo(3);
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