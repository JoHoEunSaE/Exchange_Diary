package org.johoeunsae.exchangediary.notice.controller;

import org.apache.http.HttpHeaders;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.dto.NoticeDeleteRequestDto;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.PasswordInfo;
import org.johoeunsae.exchangediary.notice.domain.Notice;
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
import utils.testdouble.notice.TestNotice;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends E2EMvcTest {

	private final static String TITLE = "title";
	private final static String CONTENT = "content";
	private final String URL_PREFIX = "/v1/notices";
	private final String BEARER = "Bearer ";
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private Member loginUser;
	private Member otherUser;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
		LocalDateTime now = LocalDateTime.now();
		loginUser = Member.createPasswordMember(
				MemberFeatures.of("test@test.com", "test"),
				now, PasswordInfo.createWithHash("test", "test", new BCryptPasswordEncoder())
		);
		otherUser = Member.createPasswordMember(
				MemberFeatures.of("test2@test.com", "test2"),
				now, PasswordInfo.createWithHash("test2", "test2", new BCryptPasswordEncoder()));
	}


	@Nested
	@DisplayName("GET /notices")
	class GetNotices {
		private final String url = "/v1/notices";

		@Test
		@DisplayName("사용자는 자신의 알림을 조회할 수 있다.")
		void getNotices() throws Exception {
			// given
			persistHelper
					.persist(loginUser)
					.and().persist(
							TestNotice.builder()
									.member(loginUser)
									.build().asEntity(),
							TestNotice.builder()
									.member(loginUser)
									.build().asEntity()
					);

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();
			MockHttpServletRequestBuilder req = get(url)
					.header(HttpHeaders.AUTHORIZATION, BEARER + token);

			// when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.size()").value(2))
					.andExpect(jsonPath("$[0].content").value(TestNotice.DEFAULT_CONTENT))
			;
		}
	}

	@Nested
	@DisplayName("DELETE /notices")
	class DeleteNotices {
		@Test
		@DisplayName("사용자는 자신의 알림을 여러 개 지울 수 있다.")
		void deleteNotices() throws Exception {

			// given
			persistHelper.persist(loginUser).flush();
			Notice notice1 = TestNotice.builder()
					.member(loginUser)
					.build().asEntity();
			Notice notice2 = TestNotice.builder()
					.member(loginUser)
					.build().asEntity();
			persistHelper.persist(notice1, notice2).flushAndClear();

			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();
			NoticeDeleteRequestDto requestDto = NoticeDeleteRequestDto.builder()
					.noticeIds(List.of(notice1.getId(), notice2.getId()))
					.build();
			MockHttpServletRequestBuilder req = post(URL_PREFIX + "/delete")
					.header(HttpHeaders.AUTHORIZATION, BEARER + token)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType("application/json");

			// when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
			;
		}

		@DisplayName("자신의 알림이 아닌 경우 지울 수 없다.")
		@Test
		void deleteNoticesFailForbidden() throws Exception {
			//given
			persistHelper.persist(loginUser, otherUser).flush();
			Notice notice1 = TestNotice.builder()
					.member(otherUser)
					.build().asEntity();
			Notice notice2 = TestNotice.builder()
					.member(otherUser)
					.build().asEntity();
			persistHelper.persist(notice1, notice2).flushAndClear();

			//when
			NoticeDeleteRequestDto requestDto = NoticeDeleteRequestDto.builder()
					.noticeIds(List.of(notice1.getId(), notice2.getId()))
					.build();
			String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId()).getTokenValue();
			MockHttpServletRequestBuilder req = post(URL_PREFIX + "/delete")
					.header(HttpHeaders.AUTHORIZATION, BEARER + token)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType("application/json");

			//then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

	}


}