package org.johoeunsae.exchangediary.like.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
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


class LikeControllerTest extends E2EMvcTest {

	private static final String BEARER = "Bearer ";
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	private static final Long INVALID_MEMBER_ID = 123L;
	private static final Long INVALID_NOTE_ID = 123L;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
	}

	@Nested
	@DisplayName("POST /v1/likes/{noteId}")
	class createLike {

		private final String url = "/v1/likes/";
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
		@DisplayName("멤버가 일기를 좋아요합니다 - 성공")
		void createLike_성공() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			MockHttpServletRequestBuilder req = post(url + note.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isCreated())
					.andDo(ignore -> {
						List<Like> likes = em.createQuery(
										"select b from Like b where b.id.memberId = :memberId and b.id.noteId = :noteId",
										Like.class)
								.setParameter("memberId", loginUser.getId())
								.setParameter("noteId", note.getId())
								.getResultList();
						assertThat(likes.size()).isEqualTo(1);
					});
		}

		@Test
		@DisplayName("멤버가 일기를 좋아요합니다 - 실패 - 존재하지 않는 멤버")
		void createLike_실패() throws Exception {
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
		@DisplayName("멤버가 일기를 좋아요합니다 - 실패 - 존재하지 않는 노트")
		void createLike_실패2() throws Exception {
			//given
			MockHttpServletRequestBuilder req = post(url + INVALID_NOTE_ID)
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 일기를 좋아요합니다 - 실패 - 노트에 권한이 없는 멤버")
		void createLike_실패3() throws Exception {
			//given
			Member otherMember = persistHelper
					.persistAndReturn(TestMember
							.builder()
							.email("lalala@exchange.com")
							.nickname("ahah")
							.build()
							.asEntity());
			Diary diary = persistHelper
					.persistAndReturn(TestDiary.builder()
							.coverType(CoverType.COLOR)
							.masterMember(otherMember)
							.build()
							.asEntity());
			persistHelper
					.persist(TestRegistration.builder()
							.member(otherMember)
							.diary(diary)
							.build().asEntity()).flushAndClear();
			Note note = persistHelper
					.persistAndReturn(TestNote.builder()
							.visibleScope(VisibleScope.PRIVATE)
							.diaryId(diary.getId())
							.member(otherMember)
							.build()
							.asEntity());

			//given
			MockHttpServletRequestBuilder req = post(url + note.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isForbidden())
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("DELETE /v1/likes/{noteId}")
	class deleteLike {

		private final String url = "/v1/likes/";
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
		@DisplayName("좋아요를 취소합니다 - 성공")
		void deleteLike_성공() throws Exception {
			//given
			Note note = persistHelper
					.persistAndReturn(TestNote.asDefaultEntity(loginUser));

			persistHelper
					.persist(
							Like.of(loginUser, note, now)
					).flushAndClear();

			MockHttpServletRequestBuilder req = delete(url + note.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isOk())
					.andDo(ignore -> {
						List<Like> likes = em.createQuery(
										"select b from Like b where b.id.memberId = :memberId and b.id.noteId = :noteId",
										Like.class)
								.setParameter("memberId", loginUser.getId())
								.setParameter("noteId", note.getId())
								.getResultList();
						assertThat(likes.size()).isEqualTo(0);
					});
		}

		@Test
		@DisplayName("좋아요를 취소합니다 - 실패 - 좋아요가 존재하지 않음")
		void deleteLike_실패() throws Exception {
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