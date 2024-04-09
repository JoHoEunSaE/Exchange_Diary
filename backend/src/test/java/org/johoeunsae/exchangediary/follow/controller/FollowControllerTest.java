package org.johoeunsae.exchangediary.follow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;
import javax.persistence.TypedQuery;
import org.hamcrest.Matchers;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.follow.domain.Follow;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.PasswordInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.PersistHelper;
import utils.test.E2EMvcTest;


class FollowControllerTest extends E2EMvcTest {

	private final String URL_PREFIX = "/v1/follows";
	private final Long NOT_FOUND_MEMBER_ID = 100L;
	private final String bearer = "Bearer ";
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private Member testMember;
	private Member member1;
	private Member member2;
	private Member member3;
	private Member member4;
	private Member member5;
	private LocalDateTime now;
	private PersistHelper persistHelper;


	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		now = LocalDateTime.now();
		LocalDateTime now = LocalDateTime.now();
		testMember = Member.createPasswordMember(
				MemberFeatures.of("test@test.com", "test"),
				now, PasswordInfo.createWithHash("test", "test", new BCryptPasswordEncoder())
		);
		member1 = Member.createPasswordMember(MemberFeatures.of("test1@test.com", "test1"), now,
				PasswordInfo.createWithHash("test1", "test1", new BCryptPasswordEncoder()));
		member2 = Member.createPasswordMember(MemberFeatures.of("test2@test.com", "test2"), now,
				PasswordInfo.createWithHash("test2", "test2", new BCryptPasswordEncoder()));
		member3 = Member.createPasswordMember(MemberFeatures.of("test3@test.com", "test3"), now,
				PasswordInfo.createWithHash("test3", "test3", new BCryptPasswordEncoder()));
		member4 = Member.createPasswordMember(MemberFeatures.of("test4@test.com", "test4"), now,
				PasswordInfo.createWithHash("test4", "test4", new BCryptPasswordEncoder()));
		member5 = Member.createPasswordMember(MemberFeatures.of("test5@test.com", "test5"), now,
				PasswordInfo.createWithHash("test5", "test5", new BCryptPasswordEncoder()));
		persistHelper = PersistHelper.start(em);
	}

	@Nested
	@DisplayName("Get /followers/{memberId}")
	class Followers {

		private final String url = URL_PREFIX + "/followers/{memberId}";

		@Test
		@DisplayName("login fine")
		void getFollowers() throws Exception {
			// given
			// 0 -> 2, 0 -> 4
			// 1 -> 2, 3 -> 2, 4 -> 2, 5 -> 2
			// 3 -> 4, 4 ->3
			persistHelper
					.persist(testMember, member1, member2, member3, member4, member5)
					.and().persist(
							Follow.of(MemberFromTo.of(testMember, member2), now),
							Follow.of(MemberFromTo.of(testMember, member4), now),
							Follow.of(MemberFromTo.of(member1, member2), now),
							Follow.of(MemberFromTo.of(member3, member2), now),
							Follow.of(MemberFromTo.of(member4, member2), now),
							Follow.of(MemberFromTo.of(member5, member2), now),
							Follow.of(MemberFromTo.of(member3, member4), now),
							Follow.of(MemberFromTo.of(member4, member3), now))
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, member2.getId())
					.header("Authorization", bearer + token);

			// then
			Object[] memberIds = Stream.of(testMember.getId(), member1.getId(), member3.getId(),
					member4.getId(), member5.getId()).map(Long::intValue).toArray();
			Object[] followIds = Stream.of(member4.getId())
					.filter(Objects::nonNull).map(Long::intValue).toArray();
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpectAll(
							jsonPath("$.totalLength").value(5),
							jsonPath("$.result.[*].memberId", Matchers.contains(memberIds)),
							jsonPath("$.result.[?(@.isFollowing == true)].memberId",
									Matchers.contains(followIds)));

		}

		@Test
		@DisplayName("not login fine")
		void getFollowersNotLogin() throws Exception {
			// given
			// 0 -> 2, 0 -> 4
			// 1 -> 2, 3 -> 2, 4 -> 2, 5 -> 2
			// 3 -> 4, 4 ->3
			persistHelper
					.persist(member1, member2, member3, member4, member5)
					.and().persist(
							Follow.of(MemberFromTo.of(member1, member2), now),
							Follow.of(MemberFromTo.of(member3, member2), now),
							Follow.of(MemberFromTo.of(member4, member2), now),
							Follow.of(MemberFromTo.of(member5, member2), now),
							Follow.of(MemberFromTo.of(member3, member4), now),
							Follow.of(MemberFromTo.of(member4, member3), now))
					.flushAndClear();

			// when
			MockHttpServletRequestBuilder request = get(url, member2.getId());

			// then
			Object[] memberIds = Stream.of(member1.getId(), member3.getId(),
					member4.getId(), member5.getId()).map(Long::intValue).toArray();
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpectAll(
							jsonPath("$.totalLength").value(4),
							jsonPath("$.result.[*].memberId", Matchers.contains(memberIds)),
							jsonPath("$.result.[?(@.isFollowing == true)]").doesNotExist());

		}

		@Test
		@DisplayName("not found member")
		void getFollowersNotFoundMember() throws Exception {
			// given
			persistHelper
					.persist(testMember)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, NOT_FOUND_MEMBER_ID)
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("no follow")
		void getFollowersNoFollow() throws Exception {
			// given
			persistHelper
					.persist(testMember, member1)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, testMember.getId())
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(0));
		}

		@Test
		@DisplayName("login 자기 자신을 찾기")
		void getFollowersLoginSelf() throws Exception {
			// given
			// 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0
			// 0 -> 1, 0 -> 3
			persistHelper
					.persist(testMember, member1, member2, member3, member4, member5)
					.and().persist(
							Follow.of(MemberFromTo.of(member1, testMember), now),
							Follow.of(MemberFromTo.of(member2, testMember), now),
							Follow.of(MemberFromTo.of(member3, testMember), now),
							Follow.of(MemberFromTo.of(member4, testMember), now),
							Follow.of(MemberFromTo.of(testMember, member1), now),
							Follow.of(MemberFromTo.of(testMember, member3), now))
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, testMember.getId())
					.header("Authorization", bearer + token);

			// then
			Object[] memberIds = Stream.of(member1.getId(), member2.getId(), member3.getId(),
					member4.getId()).map(Long::intValue).toArray();
			Object[] followIds = Stream.of(member1.getId(), member3.getId())
					.filter(Objects::nonNull).map(Long::intValue).toArray();
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpectAll(
							jsonPath("$.totalLength").value(4),
							jsonPath("$.result.[*].memberId", Matchers.contains(memberIds)),
							jsonPath("$.result.[?(@.isFollowing == true)].memberId",
									Matchers.contains(followIds)));

		}
	}


	@Nested
	@DisplayName("Get /followings/{memberId}")
	class Followings {

		private final String url = URL_PREFIX + "/followings/{memberId}";

		@Test
		@DisplayName("login fine")
		void getFollowings() throws Exception {
			// given
			// 0 -> 2, 0 -> 4
			// 1 -> 2, 1 -> 3, 1 -> 4, 1 -> 5
			// 3 -> 4, 4 -> 3
			persistHelper
					.persist(testMember, member1, member2, member3, member4, member5)
					.and().persist(
							Follow.of(MemberFromTo.of(testMember, member2), now),
							Follow.of(MemberFromTo.of(testMember, member4), now),
							Follow.of(MemberFromTo.of(member1, member2), now),
							Follow.of(MemberFromTo.of(member1, member3), now),
							Follow.of(MemberFromTo.of(member1, member4), now),
							Follow.of(MemberFromTo.of(member1, member5), now))
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, member1.getId())
					.header("Authorization", bearer + token);

			// then
			Object[] memberIds = Stream.of(member2.getId(), member3.getId(), member4.getId(),
							member5.getId())
					.filter(Objects::nonNull).map(Long::intValue).toArray();
			Object[] followIds = Stream.of(member2.getId(), member4.getId())
					.filter(Objects::nonNull).map(Long::intValue).toArray();
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpectAll(
							jsonPath("$.totalLength").value(4),
							jsonPath("$.result.[*].memberId", Matchers.contains(memberIds)),
							jsonPath("$.result.[?(@.isFollowing == true)].memberId",
									Matchers.contains(followIds)));

		}

		@Test
		@DisplayName("not login fine")
		void getFollowingsNotLogin() throws Exception {
			// given
			// 0 -> 2, 0 -> 4
			// 1 -> 2, 1 -> 3, 1 -> 4, 1 -> 5
			// 3 -> 4, 4 -> 3
			persistHelper
					.persist(member1, member2, member3, member4, member5)
					.and().persist(
							Follow.of(MemberFromTo.of(member1, member2), now),
							Follow.of(MemberFromTo.of(member1, member3), now),
							Follow.of(MemberFromTo.of(member1, member4), now),
							Follow.of(MemberFromTo.of(member1, member5), now))
					.flushAndClear();

			// when
			MockHttpServletRequestBuilder request = get(url, member1.getId());

			// then
			Object[] memberIds = Stream.of(member2.getId(), member3.getId(), member4.getId(),
							member5.getId())
					.filter(Objects::nonNull).map(Long::intValue).toArray();
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpectAll(
							jsonPath("$.totalLength").value(4),
							jsonPath("$.result.[*].memberId", Matchers.contains(memberIds)),
							jsonPath("$.result.[?(@.isFollowing == true)]").doesNotExist());

		}

		@Test
		@DisplayName("not found member")
		void getFollowingsNotFoundMember() throws Exception {
			// given
			persistHelper
					.persist(testMember)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, NOT_FOUND_MEMBER_ID)
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("no follow")
		void getFollowingsNoFollow() throws Exception {
			// given
			persistHelper
					.persist(testMember, member1)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, testMember.getId())
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(0));
		}

		@Test
		@DisplayName("login 자기 자신을 찾기")
		void getFollowingsLoginSelf() throws Exception {
			// given
			// 0 -> 1, 0 -> 3, 0 -> 4
			// 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0
			persistHelper
					.persist(testMember, member1, member2, member3, member4, member5)
					.and().persist(
							Follow.of(MemberFromTo.of(testMember, member1), now),
							Follow.of(MemberFromTo.of(testMember, member3), now),
							Follow.of(MemberFromTo.of(testMember, member4), now),
							Follow.of(MemberFromTo.of(member1, testMember), now),
							Follow.of(MemberFromTo.of(member2, testMember), now),
							Follow.of(MemberFromTo.of(member3, testMember), now),
							Follow.of(MemberFromTo.of(member4, testMember), now))
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = get(url, testMember.getId())
					.header("Authorization", bearer + token);

			// then
			Object[] memberIds = Stream.of(member1.getId(), member3.getId(), member4.getId())
					.map(Long::intValue).toArray();
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpectAll(
							jsonPath("$.totalLength").value(memberIds.length),
							jsonPath("$.result.[*].memberId", Matchers.contains(memberIds)),
							jsonPath("$.result.[?(@.isFollowing == true)].memberId",
									Matchers.contains(memberIds)));
		}
	}

	@Nested
	@DisplayName("Delete /{memberId}")
	class DeleteFollow {

		private final String url = URL_PREFIX + "/{memberId}";

		@Test
		@DisplayName("fine")
		void deleteFollow() throws Exception {
			// given
			persistHelper
					.persist(testMember, member1, member2)
					.and().persist(
							Follow.of(MemberFromTo.of(testMember, member1), now),
							Follow.of(MemberFromTo.of(testMember, member2), now))
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = delete(url, member1.getId())
					.header("Authorization", bearer + token);

			// then
			TypedQuery<Follow> targetCheckQuery = em.createQuery(
							"select f from Follow f where f.from.id = :fromId and f.to.id = :toId",
							Follow.class)
					.setParameter("fromId", testMember.getId())
					.setParameter("toId", member1.getId());
			TypedQuery<Follow> remainCheckQuery = em.createQuery(
							"select f from Follow f where f.from.id = :fromId and f.to.id = :toId",
							Follow.class)
					.setParameter("fromId", testMember.getId())
					.setParameter("toId", member2.getId());
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andDo(ignore -> assertThat(targetCheckQuery.getResultList()).isEmpty())
					.andDo(ignore -> assertThat(remainCheckQuery.getResultList()).hasSize(1));
		}

		@Test
		@DisplayName("not found follow")
		void deleteFollowNotFoundFollow() throws Exception {
			// given
			persistHelper
					.persist(testMember, member1)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = delete(url, member1.getId())
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("not found member")
		void deleteFollowNotFoundMember() throws Exception {
			// given
			persistHelper
					.persist(testMember)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = delete(url, NOT_FOUND_MEMBER_ID)
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("Post /{memberId}")
	class PostFollow {

		private final String url = URL_PREFIX + "/{memberId}";

		@Test
		@DisplayName("fine")
		void postFollow() throws Exception {
			// given
			persistHelper
					.persist(testMember, member1, member2, member3, member4, member5)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = post(url, member2.getId())
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isOk())
					.andDo(ignore -> {
						TypedQuery<Follow> query = em.createQuery(
										"select f from Follow f where f.from.id = :fromId and f.to.id = :toId",
										Follow.class)
								.setParameter("fromId", testMember.getId())
								.setParameter("toId", member2.getId());
						assertThat(query.getResultList()).hasSize(1);
					});
		}

		@Test
		@DisplayName("not found member")
		void postFollowNotFoundMember() throws Exception {
			// given
			persistHelper
					.persist(testMember)
					.flushAndClear();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = post(url, NOT_FOUND_MEMBER_ID)
					.header("Authorization", bearer + token);

			// then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("request follow with previously requested follow")
		void postFollowDuplicated() throws Exception {
			// given
			persistHelper
					.persist(testMember, member1).flushAndClear();
			String testMemberToken = jwtTokenProvider
					.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = post(url, member1.getId())
					.header("Authorization", bearer + testMemberToken);
			mockMvc.perform(request)
					.andExpect(status().isOk());

			//then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("request self follow is bad request")
		void selfFollow() throws Exception {
			// given
			persistHelper
					.persist(testMember).flushAndClear();
			String testMemberToken = jwtTokenProvider
					.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// when
			MockHttpServletRequestBuilder request = post(url, testMember.getId())
					.header("Authorization", bearer + testMemberToken);

			//then
			mockMvc.perform(request)
					.andDo(print())
					.andExpect(status().isBadRequest());
		}
	}
}