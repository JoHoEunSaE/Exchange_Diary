package org.johoeunsae.exchangediary.bookmark.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.block.domain.Block;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.utils.obfuscation.DataEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.diary.TestDiary;
import utils.testdouble.diary.TestRegistration;
import utils.testdouble.member.TestMember;
import utils.testdouble.note.TestNote;


class BookmarkControllerTest extends E2EMvcTest {

	private static final String BEARER = "Bearer ";
	private static final Long INVALID_MEMBER_ID = 123L;
	private static final Long INVALID_NOTE_ID = 123L;
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
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


	@Nested
	@DisplayName("GET /v1/bookmarks")
	class GetBookmarkList {

		private final String url = "/v1/bookmarks";
		private final LocalDateTime now = LocalDateTime.now();
		private String token;
		private Member loginUser;
		private Member otherMember;
		private Diary diary;
		private Diary otherDiary;


		@BeforeEach
		void setup() {
			loginUser = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();

			diary = persistHelper
					.persistAndReturn(TestDiary.builder()
							.masterMember(loginUser)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity());

			otherMember = persistHelper
					.persistAndReturn(
							TestMember.asSocialMember("email@example.com", "nickname"));

			otherDiary = persistHelper
					.persistAndReturn(TestDiary.builder()
							.masterMember(otherMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity());

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(diary)
							.build()
							.asEntity(),
					TestRegistration.builder()
							.member(otherMember)
							.diary(otherDiary)
							.build()
							.asEntity()
			).flushAndClear();

			List<Note> notes = persistHelper
					.persistAndReturn(
							Note.of(loginUser, diary.getId(), now,
									Board.of(encodingData("title1"), encodingData("content1")),
									VisibleScope.PUBLIC),
							Note.of(loginUser, diary.getId(), now,
									Board.of(encodingData("title2"), encodingData("content2")),
									VisibleScope.PUBLIC),
							Note.of(loginUser, diary.getId(), now,
									Board.of(encodingData("title3"), encodingData("content3")),
									VisibleScope.PRIVATE)
					);
			persistHelper
					.persist(
							Bookmark.of(loginUser, notes.get(0), now),
							Bookmark.of(loginUser, notes.get(1), now),
							Bookmark.of(loginUser, notes.get(2), now)
					).flushAndClear();
		}

		@Test
		@DisplayName("성공 - 북마크 목록을 조회합니다.")
		void 성공_getBookmarkList() throws Exception {
			//given
			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(otherDiary)
							.build()
							.asEntity()
			);

			Note otherNote = persistHelper.persistAndReturn(
							Note.of(otherMember, otherDiary.getId(), now,
									Board.of(encodingData("otherTitle"), encodingData("otherContent")),
									VisibleScope.PRIVATE)
					);

			persistHelper
					.persist(
							Bookmark.of(loginUser, otherNote, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(4))
					.andExpect(jsonPath("$.result[0].title").value("title1"))
					.andExpect(jsonPath("$.result[1].preview").value("content2"))
					.andExpect(jsonPath("$.result[2].visibleScope").value("PRIVATE"));
		}

		@Test
		@DisplayName("성공 - 북마크를 했었지만 현재는 권한이 없는 일기장에 일기가 존재하는 경우(PRIVATE)")
		void 성공2_getBookmarkList() throws Exception {
			//given
			Note otherNote = persistHelper.persistAndReturn(
					Note.of(otherMember, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PRIVATE)
			);

			persistHelper
					.persist(
							Bookmark.of(loginUser, otherNote, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(3));
		}

		@Test
		@DisplayName("성공 - 북마크를 했었지만 현재는 권한이 없는 일기장에 일기가 존재하는 경우(PUBLIC)")
		void 성공3_getBookmarkList() throws Exception {
			//given

			Note otherNote = persistHelper.persistAndReturn(
					Note.of(otherMember, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PUBLIC)
			);

			persistHelper
					.persist(
							Bookmark.of(loginUser, otherNote, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(4))
					.andExpect(jsonPath("$.totalLength").value(4));
		}

		@Test
		@DisplayName("성공 - 북마크를 했었지만 일기가 블라인드 처리 된 경우(PUBLIC_BLIND)")
		void 성공4_getBookmarkList() throws Exception {
			//given
			Note otherNote = persistHelper.persistAndReturn(
					Note.of(otherMember, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PUBLIC_BLIND)
			);

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(otherDiary)
							.build()
							.asEntity()
			);

			persistHelper
					.persist(
							Bookmark.of(loginUser, otherNote, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(3));
		}

		@Test
		@DisplayName("성공 - 북마크를 했었지만 일기가 블라인드 처리 된 경우(PRIVATE_BLIND)")
		void 성공5_getBookmarkList() throws Exception {
			//given
			Note otherNote = persistHelper.persistAndReturn(
					Note.of(otherMember, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PRIVATE_BLIND)
			);

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(otherDiary)
							.build()
							.asEntity()
			);

			persistHelper
					.persist(
							Bookmark.of(loginUser, otherNote, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(3));
		}

		@Test
		@DisplayName("성공 - 북마크를 했었지만 일기가 삭제된 경우")
		void 성공6_getBookmarkList() throws Exception {
			//given
			Note otherNote = persistHelper.persistAndReturn(
					Note.of(otherMember, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PUBLIC)
			);

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(otherDiary)
							.build()
							.asEntity()
			);

			persistHelper
					.persist(
							Bookmark.of(loginUser, otherNote, now)
					).flushAndClear();

			em.createQuery("update Note n set n.deletedAt = :deletedAt where n.id = :noteId")
					.setParameter("deletedAt", now)
					.setParameter("noteId", otherNote.getId())
					.executeUpdate();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(3));
		}

		@Test
		@DisplayName("성공 - 내 일기를 북마크를 했었지만 일기가 삭제된 경우")
		void 성공7_getBookmarkList() throws Exception {
			//given
			Note deleteNote = persistHelper.persistAndReturn(
					Note.of(loginUser, diary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PUBLIC)
			);

			persistHelper
					.persist(
							Bookmark.of(loginUser, deleteNote, now)
					).flushAndClear();

			em.createQuery("update Note n set n.deletedAt = :deletedAt where n.id = :noteId")
					.setParameter("deletedAt", now)
					.setParameter("noteId", deleteNote.getId())
					.executeUpdate();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3));
		}

		@Test
		@DisplayName("성공 - 북마크한 일기가 없는 경우")
		void 성공8_getBookmarkList() throws Exception {
			//given
			String otherMemberToken = jwtTokenProvider.createCommonAccessToken(otherMember.getId()).getTokenValue();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + otherMemberToken);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(0))
					.andExpect(jsonPath("$.totalLength").value(0));
		}

		@Test
		@DisplayName("성공 - 북마크를 했었지만 멤버를 차단한 경우")
		void 성공9_getBookmarkList() throws Exception {
			//given
			String otherMemberToken = jwtTokenProvider.createCommonAccessToken(otherMember.getId()).getTokenValue();

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(otherDiary)
							.build()
							.asEntity()
			);

			Note otherNote = persistHelper.persistAndReturn(
					Note.of(loginUser, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PUBLIC)
			);

			persistHelper
					.persist(
							Bookmark.of(otherMember, otherNote, now),
							Block.of(MemberFromTo.of(otherMember, loginUser), now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + otherMemberToken);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(1))
					.andExpect(jsonPath("$.result[0].isBlocked").value(true))
					.andExpect(jsonPath("$.totalLength").value(1));
		}

		@Test
		@DisplayName("성공 - 북마크를 한 노트의 좋아요 개수가 2개인 경우")
		void 성공10_getBookmarkList() throws Exception {
			//given
			String otherMemberToken = jwtTokenProvider.createCommonAccessToken(otherMember.getId()).getTokenValue();

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginUser)
							.diary(otherDiary)
							.build()
							.asEntity()
			);

			Note otherNote = persistHelper.persistAndReturn(
					Note.of(loginUser, otherDiary.getId(), now,
							Board.of(encodingData("otherTitle"), encodingData("otherContent")),
							VisibleScope.PUBLIC)
			);

			persistHelper
					.persist(
							Bookmark.of(otherMember, otherNote, now),
							Block.of(MemberFromTo.of(otherMember, loginUser), now),
							Like.of(otherMember, otherNote, now),
							Like.of(loginUser, otherNote, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = get(url)
					.param("page", "0")
					.param("size", "10")
					.header("Authorization", BEARER + otherMemberToken);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(1))
					.andExpect(jsonPath("$.result[0].likeCount").value(2))
					.andExpect(jsonPath("$.totalLength").value(1));
		}
	}

	@Nested
	@DisplayName("POST /v1/bookmarks/{noteId}")
	class createBookmark {

		private final String url = "/v1/bookmarks/";
		private final LocalDateTime now = LocalDateTime.now();

		private String token;
		private Member loginUser;

		@BeforeEach
		void setup() {
			loginUser = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();
		}

		@Test
		@DisplayName("멤버가 일기를 북마크합니다 - 성공")
		void createBookmark_성공() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			MockHttpServletRequestBuilder req = post(url + note.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isCreated())
					.andDo(ignore -> {
						List<Bookmark> bookmarks = em.createQuery(
										"select b from Bookmark b where b.id.memberId = :memberId and b.id.noteId = :noteId",
										Bookmark.class)
								.setParameter("memberId", loginUser.getId())
								.setParameter("noteId", note.getId())
								.getResultList();
						assertThat(bookmarks.size()).isEqualTo(1);
					});
		}

		@Test
		@DisplayName("멤버가 일기를 북마크합니다 - 실패 - 존재하지 않는 멤버")
		void createBookmark_실패() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			String notFoundToken = jwtTokenProvider.createCommonAccessToken(INVALID_MEMBER_ID)
					.getTokenValue();

			MockHttpServletRequestBuilder req = post(url + note.getId())
					.header("Authorization", BEARER + notFoundToken);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 일기를 북마크합니다 - 실패 - 존재하지 않는 노트")
		void createBookmark_실패2() throws Exception {
			//given
			MockHttpServletRequestBuilder req = post(url + INVALID_NOTE_ID)
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("DELETE /v1/bookmarks/{noteId}")
	class deleteBookmark {

		private final String url = "/v1/bookmarks/";
		private final LocalDateTime now = LocalDateTime.now();

		private String token;
		private Member loginUser;

		@BeforeEach
		void setup() {
			loginUser = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();
		}

		@Test
		@DisplayName("북마크를 취소합니다. - 성공")
		void deleteBookmark_성공() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			persistHelper
					.persist(
							Bookmark.of(loginUser, note, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = delete(url + note.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isOk())
					.andDo(ignore -> {
						List<Bookmark> bookmarks = em.createQuery(
										"select b from Bookmark b where b.id.memberId = :memberId and b.id.noteId = :noteId",
										Bookmark.class)
								.setParameter("memberId", loginUser.getId())
								.setParameter("noteId", note.getId())
								.getResultList();
						assertThat(bookmarks.size()).isEqualTo(0);
					});
		}

		@Test
		@DisplayName("멤버가 일기를 북마크합니다 - 실패 - 존재하지 않는 멤버")
		void deleteBookmark_실패() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			persistHelper
					.persist(
							Bookmark.of(loginUser, note, now)
					).flushAndClear();

			String notFoundToken = jwtTokenProvider.createCommonAccessToken(INVALID_MEMBER_ID)
					.getTokenValue();

			MockHttpServletRequestBuilder req = delete(url + note.getId())
					.header("Authorization", BEARER + notFoundToken);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 일기를 북마크합니다 - 실패 - 존재하지 않는 노트")
		void deleteBookmark_실패2() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			persistHelper
					.persist(
							Bookmark.of(loginUser, note, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = delete(url + INVALID_NOTE_ID)
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 일기를 북마크합니다 - 실패 - 북마크가 존재하지 않음")
		void deleteBookmark_실패3() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			MockHttpServletRequestBuilder req = delete(url + note.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}
	}
}