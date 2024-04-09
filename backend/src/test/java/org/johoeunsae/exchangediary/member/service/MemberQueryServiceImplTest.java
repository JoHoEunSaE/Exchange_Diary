package org.johoeunsae.exchangediary.member.service;

import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.follow.repository.FollowRepository;
import org.johoeunsae.exchangediary.follow.service.FollowService;
import org.johoeunsae.exchangediary.mapper.MemberMapper;
import org.johoeunsae.exchangediary.member.domain.*;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import utils.test.UnitTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MemberQueryServiceImplTest extends UnitTest {
	private final FollowRepository followRepository = mock(FollowRepository.class);
	private final FollowService followService = mock(FollowService.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final RegistrationRepository registrationRepository = mock(RegistrationRepository.class);
	private final MemberMapper memberMapper = mock(MemberMapper.class);
	@InjectMocks
	private MemberQueryServiceImpl memberQueryService;

	@Nested
	class FindSocialMemberByEmailTest {
		@Test
		void 정상() throws Exception {
			final String email = "social email";
			LocalDateTime now = LocalDateTime.now();
			// given
			given(memberRepository.findMemberByEmail(email))
					.willReturn(Optional.of(
							Member.createSocialMember(MemberFeatures.of(email, "name"),
									now,
									OauthInfo.of("provider", OauthType.NAVER)))
					);
			// when
			Optional<SocialMember> member = memberQueryService.findSocialMemberByEmail(email);
			// then
			assertThat(member).isPresent();
		}

		@Test
		void 소셜_멤버가_아님() throws Exception {
			final String email = "not social email";
			final LocalDateTime now = LocalDateTime.now();
			// given
			given(memberRepository.findMemberByEmail(email))
					.willReturn(Optional.of(
							Member.createPasswordMember(MemberFeatures.of(email, "name"),
									now,
									PasswordInfo.createWithHash("username", "password", new BCryptPasswordEncoder()))));
			// when
			Optional<SocialMember> member = memberQueryService.findSocialMemberByEmail(email);
			// then
			assertThat(member).isEmpty();
		}

		@Test
		void 멤버_없음() throws Exception {
			// given
			given(memberRepository.findMemberByEmail(anyString()))
					.willReturn(Optional.empty());
			// when
			Optional<SocialMember> member = memberQueryService.findSocialMemberByEmail("any");
			// then
			assertThat(member).isEmpty();
		}
	}
}