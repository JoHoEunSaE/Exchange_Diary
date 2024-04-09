package org.johoeunsae.exchangediary.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.johoeunsae.exchangediary.auth.jwt.JwtLoginTokenDto;
import org.johoeunsae.exchangediary.auth.jwt.JwtLoginTokenDto.Fields;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterRequestDTO;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginFactory;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberToken;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.notice.domain.DeviceRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import utils.JsonMatcher;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.auth.TestOauth2LoginInfoVO;
import utils.testdouble.auth.TestOauth2LoginRequestVO;
import utils.testdouble.auth.TestUnregisterRequestDTO;
import utils.testdouble.member.TestMember;
import utils.testdouble.member.TestMemberToken;

class AuthControllerTest extends E2EMvcTest {

	@MockBean
	private Oauth2LoginFactory oauth2LoginFactory;
	private Oauth2Login login;
	private Oauth2LoginInfoVO loginVo;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private PersistHelper persistHelper;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
		login = mock(Oauth2Login.class);
		loginVo = TestOauth2LoginInfoVO.asNaver();

		doCallRealMethod().when(oauth2LoginFactory).initSuppliers();

		when(login.isValid()).thenReturn(true);
		when(login.provideLoginInfo()).thenReturn(loginVo);
		when(oauth2LoginFactory.create(any())).thenReturn(Optional.of(login));
	}

	private void loginVOSettings(OauthType oauthType) {
		if (oauthType.equals(OauthType.NAVER)) {
			this.loginVo = TestOauth2LoginInfoVO.asNaver();
		} else if (oauthType.equals(OauthType.KAKAO)) {
			this.loginVo = TestOauth2LoginInfoVO.asKakao();
		} else if (oauthType.equals(OauthType.GOOGLE)) {
			this.loginVo = TestOauth2LoginInfoVO.asGoogle();
		} else if (oauthType.equals(OauthType.APPLE)) {
			this.loginVo = TestOauth2LoginInfoVO.asApple();
		}
		when(login.provideLoginInfo()).thenReturn(loginVo);
	}

	@DisplayName("다른 SNS 같은 이메일로 로그인")
	@Test
	void secondLoginWithSameEmailAndOtherSocial() throws Exception {

		JsonMatcher response = JsonMatcher.create();
		Oauth2LoginRequestVO kakaoLoginRequest = TestOauth2LoginRequestVO.asKakaoReqeust();
		Oauth2LoginRequestVO naverLoginRequest = TestOauth2LoginRequestVO.asNaverRequest();

		loginVOSettings(OauthType.KAKAO);
		// first login
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								kakaoLoginRequest)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(response.get(JwtLoginTokenDto.Fields.accessToken).is().exists())
				.andExpect(response.get(JwtLoginTokenDto.Fields.accessToken).is().isString());

		// second login
		loginVOSettings(OauthType.NAVER);
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(naverLoginRequest)))
				.andDo(print())
				.andExpect(status().isConflict())
				.andDo((e) ->
						assertThat(em.createQuery("select m from Member m", Member.class)
								.getResultList().size()).isEqualTo(1))
				.andDo((e) ->
						assertThat(em.createQuery("select d from DeviceRegistry d",
								DeviceRegistry.class).getResultList().size()).isEqualTo(1));
	}

	@DisplayName("다른 SNS 같은 이메일로 로그인 - 카카오 / 애플")
	@Test
	void secondLoginWithSameEmailAndOtherSocialApple() throws Exception {

		JsonMatcher response = JsonMatcher.create();
		Oauth2LoginRequestVO kakaoLoginRequest = TestOauth2LoginRequestVO.asNaverRequest();
		Oauth2LoginRequestVO appleLoginRequest = TestOauth2LoginRequestVO.asAppleRequest();

		loginVOSettings(OauthType.KAKAO);
		// first login
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								kakaoLoginRequest)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(response.get(JwtLoginTokenDto.Fields.accessToken).is().exists())
				.andExpect(response.get(JwtLoginTokenDto.Fields.accessToken).is().isString());

		// second login
		loginVOSettings(OauthType.APPLE);
		mockMvc.perform(post("/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(appleLoginRequest)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andDo((e) ->
						assertThat(em.createQuery("select m from Member m", Member.class)
								.getResultList().size()).isEqualTo(1))
				.andDo((e) ->
						assertThat(em.createQuery("select d from DeviceRegistry d",
								DeviceRegistry.class).getResultList().size()).isEqualTo(1));
	}

	@Nested
	@DisplayName("/v1/auth/login")
	class Login {

		@DisplayName("첫 로그인")
		@Test
		void firstLogin() throws Exception {
			// given
			JsonPathResultMatchers jsonPathResultMatchers = jsonPath("$.accessToken");
			mockMvc.perform(post("/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
									Oauth2LoginRequestVO.builder()
											.deviceToken(loginVo.getDeviceToken())
											.oauthType(loginVo.getOauthType())
											.valid("valid")
											.build())))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.accessToken").exists())
					.andExpect(jsonPathResultMatchers.isString())
					.andDo((e) ->
							assertThat(em.createQuery("select m from Member m", Member.class)
									.getResultList().size()).isEqualTo(1))
					.andDo((e) ->
							assertThat(em.createQuery("select d from DeviceRegistry d",
									DeviceRegistry.class).getResultList().size()).isEqualTo(1));
		}

		@DisplayName("두번째 로그인")
		@Test
		void secondLogin() throws Exception {
			// first login
			mockMvc.perform(post("/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
									Oauth2LoginRequestVO.builder()
											.deviceToken(loginVo.getDeviceToken())
											.oauthType(loginVo.getOauthType())
											.valid("valid")
											.build())))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.accessToken").exists())
					.andExpect(JsonMatcher.create().get(Fields.accessToken).is().isString());
//					.andExpect(jsonPath("$.accessToken").isString());

			// second login
			mockMvc.perform(post("/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
									Oauth2LoginRequestVO.builder()
											.deviceToken(loginVo.getDeviceToken())
											.oauthType(loginVo.getOauthType())
											.valid("valid")
											.build())))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.accessToken").exists())
					.andExpect(jsonPath("$.accessToken").isString())
					.andDo((e) ->
							assertThat(em.createQuery("select m from Member m", Member.class)
									.getResultList().size()).isEqualTo(1))
					.andDo((e) ->
							assertThat(em.createQuery("select d from DeviceRegistry d",
									DeviceRegistry.class).getResultList().size()).isEqualTo(1));
		}
	}

	@Nested
	@DisplayName("/v1/auth/unregister")
	class Unregister {

		@DisplayName("회원탈퇴 - 성공(카카오)")
		@Test
		void fineKakao() throws Exception {

			// given
			UnregisterRequestDTO unregisterRequestDTO = TestUnregisterRequestDTO.asKakaoReqeust();
			Member testMember = TestMember.asSocialMember(OauthType.KAKAO);
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(unregisterRequestDTO)))
					.andDo(print())
					.andExpect(status().isOk())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(1));
		}

		@DisplayName("회원탈퇴 - 성공(네이버)")
		@Test
		void fineNaver() throws Exception {
			// given
			UnregisterRequestDTO unregisterRequestDTO = TestUnregisterRequestDTO.asNaverRequest();
			Member testMember = TestMember.asSocialMember(OauthType.NAVER);
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(unregisterRequestDTO)))
					.andDo(print())
					.andExpect(status().isOk())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(1));
		}

		@DisplayName("회원탈퇴 - 성공(구글)")
		@Test
		void fineGoogle() throws Exception {
			// given
			UnregisterRequestDTO unregisterRequestDTO = TestUnregisterRequestDTO.asGoogleRequest();
			Member testMember = TestMember.asSocialMember(OauthType.NAVER);
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(unregisterRequestDTO))
					)
					.andDo(print())
					.andExpect(status().isOk())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(1));
		}

		@DisplayName("회원탈퇴 - 성공(애플)")
		@Test
		void fineApple() throws Exception {
			// given
			UnregisterRequestDTO unregisterRequestDTO = TestUnregisterRequestDTO.asAppleRequest();

			// TestMember Create
			Member testMember = TestMember.asSocialMember(OauthType.APPLE);
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			// Apple Refresh Token save
			MemberToken testMemberToken = TestMemberToken.asEntity(testMember, token);
			em.persist(testMemberToken);

			//When
			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(unregisterRequestDTO)))
					.andDo(print())
					//Then
					.andExpect(status().isOk())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(1));
		}

		@DisplayName("회원탈퇴 - social 정보 없음")
		@Test
		void badRequest() throws Exception {
			// given
			UnregisterRequestDTO unregisterRequestDTO = null;
			Member testMember = TestMember.asDefaultEntity();
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(unregisterRequestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(0));
		}

		@DisplayName("회원탈퇴 - 애플 토큰정보 없음")
		@Test
		void badRequestNoToken() throws Exception {
			// given
			UnregisterRequestDTO appleUnregisterRequest = TestUnregisterRequestDTO.asAppleRequest();
			Member testMember = TestMember.asDefaultEntity();
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(appleUnregisterRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(0));
		}

		@DisplayName("회원탈퇴 - 소셜회원이 아닐경우")
		@Test
		void fineNoSocial() throws Exception {
			// given
			UnregisterRequestDTO appleUnregisterRequest = TestUnregisterRequestDTO.asNoneSocialRequest();
			Member testMember = TestMember.asDefaultEntity();
			em.persist(testMember);
			String token = jwtTokenProvider.createCommonAccessToken(testMember.getId())
					.getTokenValue();

			mockMvc.perform(post("/v1/auth/unregister")
							.header("Authorization", "Bearer " + token)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(appleUnregisterRequest)))
					.andDo(print())
					.andExpect(status().isOk())
					.andDo((e) ->
							assertThat(em.createQuery(
											"select m from Member m where m.deletedAt is not null",
											Member.class)
									.getResultList().size()).isEqualTo(1));
		}

		@DisplayName("다른 기기로 로그인 - 디바이스 수 상한선 이상 시, 가장 오래된 디바이스 삭제 및 새로운 디바이스 토큰 추가")
		@Test
		void fineDeviceRegistry() throws Exception {
			// given
			JsonMatcher response = JsonMatcher.create();
			Oauth2LoginRequestVO kakaoLoginRequest = TestOauth2LoginRequestVO.asKakaoReqeust();
			loginVOSettings(OauthType.KAKAO);
			// first login
			mockMvc.perform(post("/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
									kakaoLoginRequest)))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(response.get(JwtLoginTokenDto.Fields.accessToken).is().exists())
					.andExpect(response.get(JwtLoginTokenDto.Fields.accessToken).is().isString());

			Member member = em.createQuery(
					"select m from Member m inner join fetch m.deviceRegistries",
					Member.class).getResultList().get(0);
			List<DeviceRegistry> registries = persistHelper.persistAndReturn(
					DeviceRegistry.of(member, "deviceToken2", LocalDateTime.now()),
					DeviceRegistry.of(member, "deviceToken3", LocalDateTime.now()),
					DeviceRegistry.of(member, "deviceToken4", LocalDateTime.now()),
					DeviceRegistry.of(member, "deviceToken5", LocalDateTime.now())
			);

			// sixth login
			mockMvc.perform(post("/v1/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(
									Oauth2LoginRequestVO.builder()
											.deviceToken(loginVo.getDeviceToken())
											.oauthType(loginVo.getOauthType())
											.valid("valid")
											.build())))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.accessToken").exists())
					.andExpect(jsonPath("$.accessToken").isString())
					.andDo((e) ->
							assertThat(em.createQuery("select m from Member m", Member.class)
									.getResultList().size()).isEqualTo(1))
					.andDo((e) -> {
						List<DeviceRegistry> queriedDeviceRegistries = em.createQuery(
								"select d from DeviceRegistry d order by d.createdAt asc",
								DeviceRegistry.class).getResultList();
//						디바이스 수가 5개인지 확인. (가장 오래된 디바이스 삭제)
						assertThat(queriedDeviceRegistries.size()).isEqualTo(5);
//						가장 첫번째 디바이스가 deviceToken2 인지 확인.
						assertThat(queriedDeviceRegistries.get(0).getToken())
								.isEqualTo("deviceToken2");
					});


		}
	}
}
