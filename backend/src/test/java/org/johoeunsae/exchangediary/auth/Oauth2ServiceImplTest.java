package org.johoeunsae.exchangediary.auth;

import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.OAUTH_BAD_GATEWAY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Optional;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.auth.oauth2.service.Oauth2Manager;
import org.johoeunsae.exchangediary.auth.oauth2.service.Oauth2ServiceImpl;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.member.domain.SocialMember;
import org.johoeunsae.exchangediary.member.service.MemberQueryService;
import org.johoeunsae.exchangediary.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.oauth2.jwt.Jwt;
import utils.test.UnitTest;
import utils.testdouble.auth.TestOauth2LoginInfoVO;
import utils.testdouble.auth.TestOauth2LoginRequestVO;
import utils.testdouble.member.TestMember;

public class Oauth2ServiceImplTest extends UnitTest {

	@InjectMocks
	Oauth2ServiceImpl oauth2ServiceImpl;
	@Mock
	Oauth2Manager oauth2Manager;
	@Mock
	MemberQueryService memberQueryService;
	@Mock
	MemberService memberService;
	@Mock
	JwtTokenProvider jwtTokenProvider;

	LocalDateTime now;

	@BeforeEach
	public void setup() {
		now = LocalDateTime.now();
	}

	@Test
	public void loginSimpleTest() {
		// given
		Oauth2LoginRequestVO requestVo = TestOauth2LoginRequestVO.asKakaoReqeust();
		Oauth2LoginInfoVO loginVo = TestOauth2LoginInfoVO.asKakao();

		given(oauth2Manager.requestOauthLoginInfo(any())).willReturn(Optional.of(loginVo));
		given(memberQueryService.findSocialMemberByEmail(any())).willReturn(
				Optional.of((SocialMember) TestMember.asSocialMember(OauthType.KAKAO)));
		given(jwtTokenProvider.createCommonAccessToken(any())).willReturn(mock(Jwt.class));

		// when
		oauth2ServiceImpl.login(requestVo, now);

		// then
		then(oauth2Manager).should().requestOauthLoginInfo(any());
		then(memberQueryService).should().findSocialMemberByEmail(any());
//		then(memberService).shouldHaveNoInteractions();
		then(jwtTokenProvider).should().createCommonAccessToken(any());
	}

	@Test
	public void badGateWay() {
		// given
		Oauth2LoginRequestVO requestVo = mock(Oauth2LoginRequestVO.class);

		given(oauth2Manager.requestOauthLoginInfo(any())).willReturn(Optional.empty());

		// when
		assertThrows(OAUTH_BAD_GATEWAY.toServiceException().getClass(),
				() -> oauth2ServiceImpl.login(requestVo, now));

		// then
		then(oauth2Manager).should().requestOauthLoginInfo(any());
	}

	@DisplayName("유저를 찾지 못해서 save가 필요할 때")
	@Test
	public void saveMember() {
		// given
		Oauth2LoginRequestVO requestVo = TestOauth2LoginRequestVO.asKakaoReqeust();
		Oauth2LoginInfoVO loginVo = TestOauth2LoginInfoVO.asKakao();

		given(oauth2Manager.requestOauthLoginInfo(any())).willReturn(Optional.of(loginVo));
		given(memberQueryService.findSocialMemberByEmail(any())).willReturn(Optional.empty());
		given(memberService.createSocialMember(any())).willReturn(
				(SocialMember) TestMember.asSocialMember(OauthType.KAKAO));
		given(jwtTokenProvider.createCommonAccessToken(any())).willReturn(mock(Jwt.class));

		// when
		oauth2ServiceImpl.login(requestVo, now);

		// then
		then(oauth2Manager).should().requestOauthLoginInfo(any());
		then(memberQueryService).should().findSocialMemberByEmail(any());
		then(memberService).should().createSocialMember(any());
		then(jwtTokenProvider).should().createCommonAccessToken(any());
	}
}
