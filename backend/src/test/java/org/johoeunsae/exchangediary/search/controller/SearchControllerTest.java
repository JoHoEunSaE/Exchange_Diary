package org.johoeunsae.exchangediary.search.controller;

import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.JsonMatcher;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.follow.TestFollow;
import utils.testdouble.member.TestMember;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest extends E2EMvcTest {

	private static final String BEARER = "Bearer ";
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
	}

	@Nested
	@DisplayName("GET /v1/search/members-preview")
	class GetSearchMemberPreviews {

		private final String url = "/v1/search/members-preview";

		@Test
		@DisplayName("사용자는 특정 유저를 test가 들어간 닉네임의 member Preview를 3개 조회 한다.")
		void getSearchMemberPreview() throws Exception {
			//given
			Member loginUser = TestMember.asDefaultEntity();
			final String searchKeyword = "test";
			Member member1 = TestMember.asDefaultEntity(searchKeyword + 1);
			Member member2 = TestMember.asDefaultEntity(searchKeyword + 2);
			Member member3 = TestMember.asDefaultEntity(searchKeyword + 3);
			persistHelper
					.persist(loginUser)
					.and().persist(member1, member2, member3)
					.and().persist(
							TestFollow.ofMany(loginUser, LocalDateTime.now(),
									member1, member3))
					.flushAndClear();


			final String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			final String methodParameter = "name";
			MockHttpServletRequestBuilder req = get(url)
					.header(AUTHORIZATION, BEARER + token)
					.param(methodParameter, searchKeyword);

			JsonMatcher response = JsonMatcher.create();

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(response.get(MemberPreviewPaginationDto.Fields.totalLength).isEquals(3))
					.andExpectAll(response.get(MemberPreviewPaginationDto.Fields.result).at(0).isEquals(
									Map.of(
											MemberPreviewDto.Fields.memberId, member1.getId(),
											MemberPreviewDto.Fields.nickname, member1.getNickname(),
											MemberPreviewDto.Fields.isFollowing, true))
							// 혹은 Test 자체에서 private하게 MemberPreviewDto와 Member에 대한 Map을 반환하는 Map<String, Object>로 넣을 수도 있음
					);
		}

		@Test
		@DisplayName("결과가 없을경우")
		void getSearchMemberPreviewNoResult() throws Exception {
			//given
			Member loginUser = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());

			final String token = jwtTokenProvider.createCommonAccessToken(loginUser.getId())
					.getTokenValue();
			final String searchKeyword = "test";
			final String methodParameter = "name";

			MockHttpServletRequestBuilder req = get(url)
					.header(AUTHORIZATION, BEARER + token)
					.param(methodParameter, searchKeyword);

			//when, then
			mockMvc.perform(req)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.totalLength").value(0));
		}
	}
}