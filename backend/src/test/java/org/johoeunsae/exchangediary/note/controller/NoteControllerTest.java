package org.johoeunsae.exchangediary.note.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import org.hamcrest.Matchers;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.dto.MemberNotePreviewDto;
import org.johoeunsae.exchangediary.dto.MyNotePreviewDto;
import org.johoeunsae.exchangediary.dto.NoteImagesDeleteRequestDto;
import org.johoeunsae.exchangediary.dto.NoteUpdateDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.utils.obfuscation.DataEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.diary.TestDiary;
import utils.testdouble.diary.TestRegistration;
import utils.testdouble.member.TestMember;
import utils.testdouble.note.TestNote;
import utils.testdouble.note.TestNoteImage;

class NoteControllerTest extends E2EMvcTest {

	private static final String BEARER = "Bearer ";
	private static final String JSON_CONTENT = "application/json";
	private static final Base64.Decoder decoder = java.util.Base64.getDecoder();
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private DataEncoder dataEncoder;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
	}

	private String encodingData(String data) {
		return dataEncoder.encode(data);
	}

	/**
	 * req : {@link UserSessionDto}
	 * <br>
	 * res : {@link List} of {@link MyNotePreviewDto}
	 */
	@Nested
	@DisplayName("GET /v1/notes/members/me")
	class GetMyNotePreviews {

		private final String url = "/v1/notes/members/me";
		private final LocalDateTime now = LocalDateTime.now();

		@Test
		@DisplayName("사용자는 본인이 작성한 일기의 Preview들을 조회할 수 있다.")
		void getMyNotePreview() throws Exception {
			//given
			Member loginUser = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			String longContent = "thisistoolongforepreview";
			Note longContentNote = persistHelper
					.persist(
							TestNote.asDefaultEntity(loginUser),
							TestNote.builder()
									.member(loginUser)
									.visibleScope(VisibleScope.PRIVATE)
									.build().asEntity())
					.and().persistAndReturn(
							TestNote.builder()
									.member(loginUser)
									.content(longContent)
									.build().asEntity());

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(url)
					.header(AUTHORIZATION, BEARER + token);
			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(3))
					.andExpect(jsonPath("$.result[0].title").value(
							(TestNote.DEFAULT_TITLE)))
					.andExpect(
							jsonPath("$.result[1].visibleScope").value(VisibleScope.PRIVATE.name()))
					.andExpect(jsonPath("$.result[2].preview").value(longContentNote.getPreview()));
		}
	}

	@Nested
	@DisplayName("GET /v1/notes/public")
	class nested {

		private final String url = "/v1/notes/public";
		private final LocalDateTime now = LocalDateTime.now();
		Member loginUser = TestMember.builder()
				.nickname("nickname1")
				.email("email@naver.com")
				.build().asEntity();
		Member otherUser = TestMember.builder()
				.nickname("nickname2")
				.email("email2@naver.com")
				.build().asEntity();

		@Test
		@DisplayName("공개된 일기들의 프리뷰를 최신 순으로 조회할 수 있다.")
		void getPublicNotesPreview() throws Exception {
			//given
			persistHelper
					.persist(loginUser, otherUser)
					.and().persist(
							Note.of(loginUser, null, now,
									Board.of(encodingData("title1"), encodingData("content1")),
									VisibleScope.PUBLIC),
							Note.of(loginUser, null, now.plusHours(1),
									Board.of(encodingData("title2"), encodingData("content2")),
									VisibleScope.PUBLIC),
							Note.of(otherUser, null, now.plusHours(2),
									Board.of(encodingData("title3"), encodingData("content3")),
									VisibleScope.PUBLIC),
							Note.of(otherUser, null, now.plusHours(3),
									Board.of(encodingData("title4"), encodingData("content4")),
									VisibleScope.PRIVATE))
					.flushAndClear();

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(url)
					.header("Authorization", BEARER + token)
					.param("page", "0")
					.param("size", "2");

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(3))
					.andExpect(jsonPath("$.result[0].title").value("title3"))
					.andExpect(jsonPath("$.result.[1].title").value("title2"));
		}

		@Test
		@DisplayName("성공 - 좋아요가 2개인 경우")
		void 성공2_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			persistHelper
					.persist(loginUser, otherUser);
			Note note = persistHelper.persistAndReturn(
					Note.of(loginUser, null, now,
							Board.of(encodingData("title1"), encodingData("content1")),
							VisibleScope.PUBLIC));
			persistHelper.persist(
					Like.of(loginUser, note, now),
					Like.of(otherUser, note, now)).flushAndClear();

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(url)
					.header("Authorization", BEARER + token)
					.param("page", "0")
					.param("size", "2");

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(1))
					.andExpect(jsonPath("$.result[0].likeCount").value(2));
		}

	}


	/**
	 * req : {@link UserSessionDto}, {@link Long memberId}
	 * <br>
	 * res : {@link List} of {@link MemberNotePreviewDto}
	 */
	@Nested
	@DisplayName("GET /v1/notes/members/{memberId}")
	class GetMemberNote {

		private final String url = "/v1/notes/members/";
		private final LocalDateTime now = LocalDateTime.now();
		private final long page = 0;
		private final long size = 10;

		@Test
		@DisplayName("특정 사용자의 공개된 일기들의 preview를 조회할 수 있다")
		void getMemberNotePreview() throws Exception {
			Member loginUser = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			Member author = persistHelper
					.persistAndReturn(
							TestMember.builder().nickname("author").email("author@email.com")
									.build().asEntity());

			persistHelper.persist(
					TestNote.asDefaultEntity(loginUser),
					TestNote.builder()
							.member(author)
							.content("나는 정상인")
							.visibleScope(VisibleScope.PUBLIC)
							.build().asEntity(),
					TestNote.builder()
							.member(author)
							.content("오늘은 내 생일")
							.visibleScope(VisibleScope.PUBLIC)
							.build().asEntity(),
					TestNote.builder()
							.member(author)
							.content("행복한 하루")
							.visibleScope(VisibleScope.PUBLIC)
							.build().asEntity(),
					TestNote.builder()
							.member(author)
							.content("나의 엄청난 비밀")
							.visibleScope(VisibleScope.PRIVATE)
							.build().asEntity(),
					TestNote.builder()
							.member(author)
							.content("아무도 모르는 내 비밀")
							.visibleScope(VisibleScope.PRIVATE)
							.build().asEntity()
			);

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(
					url + author.getId() + "?page=" + page + "&size=" + size)
					.header(AUTHORIZATION, BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(3))
					.andExpect(jsonPath("$.result[0].preview").value("나는 정상인"))
					.andExpect(jsonPath("$.result[2].preview").value("행복한 하루"))
					.andExpect(jsonPath("$.result[*].preview").value(
							Matchers.not(Matchers.hasItems("나의 엄청난 비밀", "아무도 모르는 내 비밀"))))
			;
		}

	}


	/**
	 * req : {@link UserSessionDto}, {@link Long noteId}
	 * <br>
	 * res : {@link NoteViewDto}
	 */
	@Nested
	@DisplayName("GET /v1/notes/{noteId}")
	class GetNote {

		private final String url = "/v1/notes/";
		private final LocalDateTime now = LocalDateTime.now();

		Member loginUser = TestMember.asDefaultEntity();
		Member author = TestMember.builder().nickname("author").email("author@email.com")
				.build().asEntity();


		@Test
		@DisplayName("본인의 일기인 경우 일기의 상세 정보를 조회할 수 있다.")
		void getNote() throws Exception {
			persistHelper.persist(loginUser, author)
					.flushAndClear();
			Note authorPublicNote = TestNote.builder()
					.member(author)
					.build()
					.asEntity();
			Note note = persistHelper.persistAndReturn(authorPublicNote);
			persistHelper.persist(
							Bookmark.of(loginUser, note, now),
							Like.of(loginUser, note, now))
					.flushAndClear();

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(url + note.getId())
					.header(AUTHORIZATION, BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.title").value((TestNote.DEFAULT_TITLE)))
					.andExpect(jsonPath("$.content").value((TestNote.DEFAULT_CONTENT)))
					.andExpect(jsonPath("$.likeCount").value(1))
					.andExpect(jsonPath("$.isBookmarked").value(true))
					.andExpect(jsonPath("$.isLiked").value(true));
		}

		@DisplayName("비공개이고 본인의 일기는 아니지만 같은 다이어리에 속한 일기인 경우 상세 정보를 조회할 수 있다.")
		@Test
		void test3() throws Exception {
			//given
			persistHelper.persist(loginUser, author);
			Diary authorDiary = persistHelper.persistAndReturn(TestDiary.builder()
					.masterMember(author)
					.build().asEntity());
			Note authorPrivateNote = TestNote.builder()
					.member(author)
					.visibleScope(VisibleScope.PRIVATE)
					.build().asEntity();
			authorPrivateNote.updateDiaryId(authorDiary.getId());
			persistHelper.persist(authorPrivateNote)
					.and().persist(TestRegistration.builder()
							.member(loginUser)
							.diary(authorDiary)
							.build().asEntity());
			persistHelper.flushAndClear();

			//when
			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(url + authorPrivateNote.getId())
					.header(AUTHORIZATION, BEARER + token);

			//then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.title").value((TestNote.DEFAULT_TITLE)))
					.andExpect(jsonPath("$.content").value((TestNote.DEFAULT_CONTENT)));
		}

		@DisplayName("비공개이고 본인의 일기도 아니고 자신의 다이어리에 속한 일기가 아닌 경우 상세 정보를 조회할 수 없다.")
		@Test
		void test4() throws Exception {
			//given
			persistHelper.persist(loginUser, author);
			Diary authorDiary = persistHelper.persistAndReturn(TestDiary.builder()
					.masterMember(author)
					.build().asEntity());
			Note authorPrivateNote = persistHelper.persistAndReturn(TestNote.builder()
					.member(author)
					.diaryId(authorDiary.getId())
					.visibleScope(VisibleScope.PRIVATE)
					.build().asEntity());
			persistHelper.flushAndClear();

			//when
			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = get(url + authorPrivateNote.getId())
					.header(AUTHORIZATION, BEARER + token);

			//then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isForbidden());
		}
	}


	/**
	 * req : {@link UserSessionDto}, {@link Long noteId}, {@link NoteUpdateDto}
	 */
	@Nested
	@DisplayName("POST /v1/notes/{noteId}")
	class UpdateNote {

		private final String url = "/v1/notes/";

		@Test
		@DisplayName("본인의 일기를 업데이트 할 수 있다.")
		void updateNote() throws Exception {
			Member loginUser = persistHelper.persistAndReturn(TestMember.asDefaultEntity());
			Note note = persistHelper.persistAndReturn(TestNote.asDefaultEntity(loginUser));
			String titleToChange = "나는 이제 일기요 컨트롤러에 들어가게 되었도다.";
			String contentToChange = "내용이 바뀌었답니다";

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			NoteUpdateDto dto = NoteUpdateDto.builder()
					.title(titleToChange)
					.content(contentToChange)
					.build();
			MockHttpServletRequestBuilder req = patch(url + note.getId())
					.header(AUTHORIZATION, BEARER + token)
					.contentType(JSON_CONTENT)
					.content(objectMapper.writeValueAsString(dto));

			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andDo(e -> {
						Note updatedNote = em.find(Note.class, note.getId());
						assertThat(updatedNote.getTitle()).isEqualTo(
								encodingData(titleToChange));
						assertThat(updatedNote.getContent()).isEqualTo(
								encodingData(contentToChange));
					});
		}

	}

	/**
	 * req : {@link UserSessionDto}, {@link Long noteId}
	 */
	@Nested
	@DisplayName("DELETE /v1/notes/{noteId}")
	class DeleteNote {

		private final String url = "/v1/notes/";
		@Value("${spring.images.path.note}")
		public String NOTE_IMAGE_DIR;

		@Test
		@DisplayName("본인의 일기장을 삭제할 수 있다.")
		void deleteNote() throws Exception {
			Member loginUser = persistHelper.persistAndReturn(TestMember.asDefaultEntity());
			Note note = persistHelper.persistAndReturn(TestNote.asDefaultEntity(loginUser));
			List<NoteImage> noteImages = persistHelper.persistAndReturn(
					TestNoteImage.createEntitiesOf(note,
							NOTE_IMAGE_DIR + "image1.jpg",
							NOTE_IMAGE_DIR + "image2.jpg",
							NOTE_IMAGE_DIR + "image3.jpg"));
			note.addNoteImages(noteImages);
			persistHelper.flushAndClear();

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			MockHttpServletRequestBuilder req = delete(url + note.getId())
					.header(AUTHORIZATION, BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andDo(e -> {
						List<Note> deletedNote = em.createQuery(
										"select n from Note n where n.id = :noteId AND n.deletedAt IS NULL",
										Note.class)
								.setParameter("noteId", note.getId())
								.getResultList();

						List<NoteImage> deletedNoteImages = em.createQuery(
										"select ni from NoteImage ni where ni.note.id = :noteId",
										NoteImage.class)
								.setParameter("noteId", note.getId())
								.getResultList();
						assertThat(deletedNote.isEmpty()).isTrue();
						assertThat(deletedNoteImages).isEmpty();
					});
		}

	}


	/**
	 * req : {@link UserSessionDto}, {@link Long noteId}, {@link List} of
	 * {@link Integer imageIndexes}
	 */
	@Nested
	@DisplayName("DELETE /v1/notes/{noteId}/note-images")
	class DeleteNoteImages {

		private final String url = "/v1/notes/";
		private final String suffix = "/note-images";
		@Value("${spring.images.path.note}")
		public String NOTE_IMAGE_DIR;

		@Test
		@DisplayName("본인의 일기에 있는 이미지를 삭제할 수 있다.")
		void deleteNoteImages() throws Exception {
			Member loginUser = persistHelper.persistAndReturn(TestMember.asDefaultEntity());
			Note note = persistHelper.persistAndReturn(TestNote.asDefaultEntity(loginUser));
			List<NoteImage> noteImages = persistHelper.persistAndReturn(
					TestNoteImage.createEntitiesOf(note,
							NOTE_IMAGE_DIR + "image1.jpg",
							NOTE_IMAGE_DIR + "image2.jpg",
							NOTE_IMAGE_DIR + "image3.jpg"));
			note.addNoteImages(noteImages);
			persistHelper.flushAndClear();

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			NoteImagesDeleteRequestDto dto = new NoteImagesDeleteRequestDto(List.of(0, 2));
			MockHttpServletRequestBuilder req = delete(url + note.getId() + suffix)
					.header(AUTHORIZATION, BEARER + token)
					.contentType(JSON_CONTENT)
					.content(objectMapper.writeValueAsString(dto));

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andDo(e -> {
						List<NoteImage> deletedNoteImages = em.createQuery(
										"select ni from NoteImage ni where ni.note.id = :noteId",
										NoteImage.class)
								.setParameter("noteId", note.getId())
								.getResultList();
						assertThat(deletedNoteImages).hasSize(1);
						assertThat(deletedNoteImages.get(0).getImageUrl()).isEqualTo(
								NOTE_IMAGE_DIR + "image2.jpg");
					});
		}

	}

}