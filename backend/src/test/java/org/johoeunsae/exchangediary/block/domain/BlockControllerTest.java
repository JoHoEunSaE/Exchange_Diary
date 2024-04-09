package org.johoeunsae.exchangediary.block.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.member.TestMember;

public class BlockControllerTest extends E2EMvcTest {

	private static final String BEARER = "Bearer ";
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	private final String url = "/v1/blocks/";
	private final LocalDateTime now = LocalDateTime.now();

	private static final Long INVALID_MEMBER_ID = 123L;
	private static final Long INVALID_NOTE_ID = 123L;

	private Member loginUser;
	private String token;
	private Member alreadyBlockedMember;
	private Member member1;
	private Member member2;
	private Member member3;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
		loginUser = persistHelper
				.persistAndReturn(TestMember.asDefaultEntity());
		token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();
		alreadyBlockedMember = persistHelper
				.persistAndReturn(TestMember
						.asSocialMember("blocked@exchange.com", "blocked"));
		persistHelper.persist(
				Block.of(MemberFromTo.of(loginUser, alreadyBlockedMember), LocalDateTime.now())
		).flushAndClear();
		member1 = persistHelper.persistAndReturn(
				TestMember.asSocialMember("member@exchange.com", "member")
		);
		member2 = persistHelper.persistAndReturn(
				TestMember.asSocialMember("member2@exchange.com", "member2")
		);
		member3 = persistHelper.persistAndReturn(
				TestMember.asSocialMember("member3@exchange.com", "member3")
		);
	}

	@Nested
	@DisplayName("GET /v1/blocks")
	class getBlockedUsers {

		@BeforeEach
		void setup() {

		}

		@Test
		@DisplayName("멤버가 차단한 멤버 목록을 조회 - 성공")
		void getBlockedUsers() throws Exception {
			//given
			Member nick = persistHelper.persistAndReturn(
					TestMember.asSocialMember("nick@exchange.com", "nick")
			);
			persistHelper
					.persist(
							Block.of(MemberFromTo.of(loginUser, member1), now),
							Block.of(MemberFromTo.of(loginUser, member2), now),
							Block.of(MemberFromTo.of(nick, member3), now) // 다른 멤버가 차단한 멤버
					).flushAndClear();

			MockHttpServletRequestBuilder req = get("/v1/blocks")
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(3))
					.andExpect(jsonPath("$.result[0].nickname").value("blocked"));
		}

		@Test
		@DisplayName("멤버가 차단한 멤버 목록을 조회 - 성공 - 차단한 멤버가 없는 경우")
		void getBlockedUsers_성공2() throws Exception {
			Member nick = persistHelper.persistAndReturn(
					TestMember.asSocialMember("nick@exchange.com", "nick")
			);
			String otherToken = jwtTokenProvider.createCommonAccessToken(nick.getId())
					.getTokenValue();
			//given
			MockHttpServletRequestBuilder req = get("/v1/blocks")
					.header("Authorization", BEARER + otherToken);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(0));
		}
	}

	@Nested
	@DisplayName("POST /v1/blocks/{memberId}")
	class createBlock {

		@BeforeEach
		void setup() {

		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 - 성공")
		void createBlock_성공() throws Exception {
			//given

			MockHttpServletRequestBuilder req = post(url + member1.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isCreated())
					.andDo(ignore -> {
						List<Block> blocks = em.createQuery(
										"select b from Block b where b.id.memberId = :memberId",
										Block.class)
								.setParameter("memberId", loginUser.getId())
								.getResultList();
						assertThat(blocks.size()).isEqualTo(2);
					});
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 - 실패 - 존재하지 않는 멤버")
		void createBlock_실패() throws Exception {
			//given

			String notFoundToken = jwtTokenProvider.createCommonAccessToken(INVALID_MEMBER_ID)
					.getTokenValue();

			MockHttpServletRequestBuilder req = post(url + alreadyBlockedMember.getId())
					.header("Authorization", BEARER + notFoundToken);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 - 실패 - 존재하지 않는 멤버2")
		void createBlock_실패2() throws Exception {
			//given
			MockHttpServletRequestBuilder req = post(url + INVALID_MEMBER_ID)
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 - 실패 - 이미 차단한 멤버")
		void createBlock_실패3() throws Exception {
			//given
			MockHttpServletRequestBuilder req = post(url + alreadyBlockedMember.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isConflict())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 - 실패 - 본인 차단 시도")
		void createBlock_실패4() throws Exception {
			//given
			MockHttpServletRequestBuilder req = post(url + loginUser.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isForbidden())
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("DELETE /v1/blocks/{memberId}")
	class deleteBlock {

		@BeforeEach
		void setup() {

		}

		@Test
		@DisplayName("차단을 해제 - 성공")
		void deleteBlock_성공() throws Exception {
			//given
			MockHttpServletRequestBuilder req = delete(url + alreadyBlockedMember.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isOk())
					.andDo(ignore -> {
						List<Block> blocks = em.createQuery(
										"select b from Block b where b.id.memberId = :memberId",
										Block.class)
								.setParameter("memberId", loginUser.getId())
								.getResultList();
						assertThat(blocks.size()).isEqualTo(0);
					});
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 해제 - 실패 - 존재하지 않는 멤버")
		void deleteBlock_실패() throws Exception {
			//given
			String notFoundToken = jwtTokenProvider.createCommonAccessToken(INVALID_MEMBER_ID)
					.getTokenValue();

			MockHttpServletRequestBuilder req = delete(url + alreadyBlockedMember.getId())
					.header("Authorization", BEARER + notFoundToken);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 해제 - 실패 - 존재하지 않는 멤버2")
		void deleteBlock_실패2() throws Exception {
			//given
			MockHttpServletRequestBuilder req = delete(url + INVALID_NOTE_ID)
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("멤버가 다른 멤버를 차단 해제 - 실패 - 차단하지 않은 멤버")
		void deleteBlock_실패3() throws Exception {
			//given
			MockHttpServletRequestBuilder req = delete(url + member2.getId())
					.header("Authorization", BEARER + token);

			//when, then
			mockMvc.perform(req)
					.andExpect(status().isNotFound())
					.andDo(print());
		}
	}
}
