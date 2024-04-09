package org.johoeunsae.exchangediary.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginFactory;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.OauthInfo;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.member.domain.PasswordInfo;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.member.TestMember;
import utils.testdouble.member.TestMemberUpdateRequestDto;

class MemberControllerTest extends E2EMvcTest {

	private final String URL_PREFIX = "/v1/members";
	private final String bearer = "Bearer ";
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private Member testMember;
	private Member kakaoMember;
	private Member naverMember;
	private Member googleMember;
	private PersistHelper persistHelper;
	@MockBean
	Oauth2LoginFactory oauth2LoginFactory;

	@Autowired
	MemberRepository memberRepository;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		Oauth2Login login = mock(Oauth2Login.class);
		doCallRealMethod().when(oauth2LoginFactory).initSuppliers();
		when(oauth2LoginFactory.create(any())).thenReturn(Optional.of(login));

		LocalDateTime now = LocalDateTime.now();
		testMember = Member.createPasswordMember(
				MemberFeatures.of("test@test.com", "test"),
				now, PasswordInfo.createWithHash("test", "test", new BCryptPasswordEncoder())
		);
		kakaoMember = Member.createSocialMember(
				MemberFeatures.of("aaa@test.com", "aaa"),
				now, OauthInfo.of("aaa", OauthType.KAKAO));
		naverMember = Member.createSocialMember(
				MemberFeatures.of("bbb@test.com", "bbb"),
				now, OauthInfo.of("bbb", OauthType.NAVER));
		googleMember = Member.createSocialMember(
				MemberFeatures.of("ccc@test.com", "ccc"),
				now, OauthInfo.of("ccc", OauthType.GOOGLE));
		persistHelper = PersistHelper.start(em);
	}

	@Nested
	@DisplayName("Get /me/profile")
	class MeProfile {

		private final String url = URL_PREFIX + "/me/profile";

		@DisplayName("fine")
		@Test
		void fine() throws Exception {
			persistHelper
					.persist(testMember)
					.flush();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			MockHttpServletRequestBuilder request = get(url)
					.header("Authorization", bearer + token);

			mockMvc.perform(request)
					.andExpect(status().isOk())
					.andExpect(jsonPath("nickname").value(testMember.getNickname()))
					.andExpect(jsonPath("statement").value(testMember.getStatement()))
					.andExpect(jsonPath("profileImageUrl").value(testMember.getProfileImageUrl()))
					.andExpect(jsonPath("followerCount").isNumber())
					.andExpect(jsonPath("followingCount").isNumber())
					.andExpect(jsonPath("isFollowing").isBoolean());
		}

		@DisplayName("unauthorized")
		@Test
		void Unauthorized() throws Exception {
			mockMvc.perform(get(url))
					.andExpect(status().isUnauthorized());
		}
	}

	@Nested
	@DisplayName("Patch /me/profile")
	class MeProfilePatch {

		private final String url = URL_PREFIX + "/me/profile";

		@DisplayName("badRequestDuplicateNickname")
		@Test
		void badRequest() throws Exception {
			Member test = TestMember.asDefaultEntity();
			Member duplicate = TestMember.asSocialMember("ABC@naver.com", "abc");

			em.persist(test);
			em.persist(duplicate);

			String token = jwtTokenProvider.createCommonAccessToken(test.getId()).getTokenValue();
			MockHttpServletRequestBuilder request = patch(url)
					.header("Authorization", bearer + token)
					.contentType(MediaType.APPLICATION_JSON)
					.param("nickname", "abc");

			mockMvc.perform(request)
					.andExpect(status().isBadRequest());

		}

		@DisplayName("fine_DuplicateNickname")
		@Test
		void fine_SameNickname() throws Exception {
			Member test = TestMember.asDefaultEntity();
			Member duplicate = TestMember.asSocialMember("ABC@naver.com", "abc");

			em.persist(test);
			em.persist(duplicate);
			String token = jwtTokenProvider.createCommonAccessToken(test.getId()).getTokenValue();

			mockMvc.perform(patch(url)
							.header("Authorization", bearer + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
											TestMemberUpdateRequestDto
													.asDefaultDto("nickname")
									)
							))
					.andDo(print())
					.andExpect(status().isOk());

		}

//		@DisplayName("fine")
//		@Test
//		void fine() throws Exception {
//			persistHelper
//					.persist(testMember)
//					.flush();
//			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId()).getTokenValue();
//			MockHttpServletRequestBuilder request = patch(url)
//					.header("Authorization", bearer + token)
//					.contentType(MediaType.APPLICATION_JSON)
//					.param("nickname", "newName")
//					.param("statement", "newStatement");
//
//			persistHelper.clear();
//			mockMvc.perform(request)
//					.andExpect(status().isOk())
//					.andDo((e) -> {
//						Member member = em.find(Member.class, testMember.getId());
//						assertThat(member.getNickname()).isEqualTo("newName");
//						assertThat(member.getStatement()).isEqualTo("newStatement");
//					});
//		}

//		@DisplayName("fine empty")
//		@Test
//		void emptyFine() throws Exception {
//			persistHelper
//					.persist(testMember)
//					.flush();
//			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId()).getTokenValue();
//			MockHttpServletRequestBuilder request = patch(url)
//					.header("Authorization", bearer + token)
//					.contentType(MediaType.APPLICATION_JSON);
//
//			persistHelper.clear();
//			mockMvc.perform(request)
//					.andDo(print())
//					.andExpect(status().isOk());
//		}

//		@DisplayName("unauthorized")
//		@Test
//		void unauthorized() throws Exception {
//			MockHttpServletRequestBuilder request = patch(url)
//					.contentType(MediaType.APPLICATION_JSON)
//					.param("nickname", "newName")
//					.param("statement", "newStatement");
//
//			mockMvc.perform(request)
//					.andExpect(status().isUnauthorized());
//		}
	}

	@Nested
	@DisplayName("Get /{memberId}/profile")
	class Profile {

		private final String url = URL_PREFIX + "/{memberId}/profile";

		@DisplayName("fine")
		@Test
		void fine() throws Exception {
			Member testMember2 = Member.createPasswordMember(
					MemberFeatures.of("test2@test.com", "test2"),
					LocalDateTime.now(),
					PasswordInfo.createWithHash("test2", "test2", new BCryptPasswordEncoder()));
			persistHelper
					.persist(testMember, testMember2)
					.flush();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			MockHttpServletRequestBuilder request = get(url, testMember2.getId())
					.header("Authorization", bearer + token);

			mockMvc.perform(request)
					.andExpect(status().isOk())
					.andExpect(jsonPath("nickname").value(testMember2.getNickname()))
					.andExpect(jsonPath("statement").value(testMember2.getStatement()))
					.andExpect(jsonPath("profileImageUrl").value(testMember2.getProfileImageUrl()))
					.andExpect(jsonPath("followerCount").isNumber())
					.andExpect(jsonPath("followingCount").isNumber())
					.andExpect(jsonPath("isFollowing").isBoolean());
		}

		@DisplayName("not found")
		@Test
		void notFound() throws Exception {
			persistHelper
					.persist(testMember)
					.flush();
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			MockHttpServletRequestBuilder request = get(url, 9999)
					.header("Authorization", bearer + token);

			mockMvc.perform(request)
					.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("Get /me/oauthType")
	class MeOauthType {

		private final String url = URL_PREFIX + "/me/oauthType";

		@DisplayName("fine kakao")
		@Test
		void fine() throws Exception {
			persistHelper
					.persist(kakaoMember)
					.flush();
			String token = jwtTokenProvider.createCommonAccessToken(kakaoMember.getId())
					.getTokenValue();

	        MockHttpServletRequestBuilder request = get(url)
	                .header("Authorization", bearer + token);

	        mockMvc.perform(request)
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("oauthType").value(OauthType.KAKAO.name()));
	    }

		@DisplayName("fine naver")
	    @Test
	    void fine2() throws Exception {
	        persistHelper
			        .persist(naverMember)
	                .flush();
	        String token = jwtTokenProvider.createCommonAccessToken(naverMember.getId()).getTokenValue();

	        MockHttpServletRequestBuilder request = get(url)
	                .header("Authorization", bearer + token);

	        mockMvc.perform(request)
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("oauthType").value(OauthType.NAVER.name()));
	    }

		@DisplayName("fine google")
		@Test
		void fine3() throws Exception {
		 persistHelper
				 .persist(googleMember)
				 .flush();
			 String token = jwtTokenProvider.createCommonAccessToken(googleMember.getId()).getTokenValue();

			 MockHttpServletRequestBuilder request = get(url)
					 .header("Authorization", bearer + token);

			 mockMvc.perform(request)
					 .andExpect(status().isOk())
					 .andExpect(jsonPath("oauthType").value(OauthType.GOOGLE.name()));
		 }

		 @DisplayName("notfound")
		 @Test
		 void notfound() throws Exception {
			 persistHelper
					 .persist(testMember)
					 .flush();
			 String token = jwtTokenProvider.createCommonAccessToken(testMember.getId()).getTokenValue();

			 MockHttpServletRequestBuilder request = get(url)
					 .header("Authorization", bearer + token);

			 mockMvc.perform(request)
					 .andExpect(status().isNotFound());
		 }

	    @DisplayName("unauthorized")
	    @Test
	    void unauthorized() throws Exception {
	        mockMvc.perform(get(url))
	                .andExpect(status().isUnauthorized());
	    }
	}

	@Nested
	@DisplayName("Get /me/oauthType")
	class GetOauthType {
	    private final String url = URL_PREFIX + "/me/oauthType";

	    @DisplayName("fine kakao")
	    @Test
	    void fine() throws Exception {
	        persistHelper
			        .persist(kakaoMember)
	                .flush();
	        String token = jwtTokenProvider.createCommonAccessToken(kakaoMember.getId()).getTokenValue();

	        MockHttpServletRequestBuilder request = get(url)
	                .header("Authorization", bearer + token);

	        mockMvc.perform(request)
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("oauthType").value(OauthType.KAKAO.name()));
	    }

		@DisplayName("fine naver")
	    @Test
	    void fine2() throws Exception {
	        persistHelper
			        .persist(naverMember)
	                .flush();
	        String token = jwtTokenProvider.createCommonAccessToken(naverMember.getId()).getTokenValue();

	        MockHttpServletRequestBuilder request = get(url)
	                .header("Authorization", bearer + token);

	        mockMvc.perform(request)
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("oauthType").value(OauthType.NAVER.name()));
	    }

		@DisplayName("fine google")
		@Test
		void fine3() throws Exception {
		 persistHelper
				 .persist(googleMember)
				 .flush();
			 String token = jwtTokenProvider.createCommonAccessToken(googleMember.getId()).getTokenValue();

			 MockHttpServletRequestBuilder request = get(url)
					 .header("Authorization", bearer + token);

			 mockMvc.perform(request)
					 .andExpect(status().isOk())
					 .andExpect(jsonPath("oauthType").value(OauthType.GOOGLE.name()));
		 }

		 @DisplayName("notfound")
		 @Test
		 void notfound() throws Exception {
			 persistHelper
					 .persist(testMember)
					 .flush();
			 String token = jwtTokenProvider.createCommonAccessToken(testMember.getId()).getTokenValue();

			 MockHttpServletRequestBuilder request = get(url)
					 .header("Authorization", bearer + token);

			 mockMvc.perform(request)
					 .andExpect(status().isNotFound());
		 }

	    @DisplayName("unauthorized")
	    @Test
	    void unauthorized() throws Exception {
	        mockMvc.perform(get(url))
	                .andExpect(status().isUnauthorized());
	    }
	}
}