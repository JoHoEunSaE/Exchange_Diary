package org.johoeunsae.exchangediary.auth.oauth2.service;

import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.ALREADY_EXIST_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.NOT_ACTIVE_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.NOT_FOUND_APPLE_TOKEN;
import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.NOT_FOUND_SOCIAL_INFO;
import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.OAUTH_BAD_GATEWAY;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.jwt.JwtLoginTokenDto;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.auth.oauth2.domain.MemberUnregisterEvent;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterRequestDTO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.LoginResultVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberToken;
import org.johoeunsae.exchangediary.member.domain.SocialMember;
import org.johoeunsae.exchangediary.member.repository.MemberTokenRepository;
import org.johoeunsae.exchangediary.member.service.MemberQueryService;
import org.johoeunsae.exchangediary.member.service.MemberService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Oauth2ServiceImpl implements Oauth2Service {

	private final JwtTokenProvider jwtTokenProvider;
	private final Oauth2Manager oauth2Manager;
	private final MemberQueryService memberQueryService;
	private final MemberService memberService;
	private final MemberTokenRepository memberTokenRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public LoginResultVO login(Oauth2LoginRequestVO dto, LocalDateTime now) {
		log.debug("Called login: {}", dto);
		Oauth2LoginInfoVO loginInfoVO = oauth2Manager.requestOauthLoginInfo(dto)
				.orElseThrow(OAUTH_BAD_GATEWAY::toServiceException);
		log.info("loginInfoVO: {}", loginInfoVO);

		boolean isNew = false;
		Optional<SocialMember> optionalSocialMember = getSocialMember(loginInfoVO);
		if (optionalSocialMember.isEmpty()) {
			optionalSocialMember = saveSocialMember(loginInfoVO);
			optionalSocialMember.orElseThrow(ALREADY_EXIST_MEMBER::toServiceException);
			isNew = true;
		}
		SocialMember socialMember = optionalSocialMember.get();
		/**
		 * 이미 다른 소셜로 로그인한 경우
		 */
		if (!socialMember.getOauthType().equals(loginInfoVO.getOauthType())) {
			oauth2TokenRevoke(UserSessionDto.builder().userId(socialMember.getId())
					.roles(List.of(socialMember.getRole())).build(), new UnregisterRequestDTO(dto));

			throw ALREADY_EXIST_MEMBER.toServiceException();
		}
		/**
		 * 탈퇴 처리된 멤버인경우
		 */
		if (!socialMember.isActive()) {
			throw NOT_ACTIVE_MEMBER.toServiceException();
		}
		memberService.upsertDeviceRegistry(socialMember, dto.getDeviceToken(), now);
		socialMember.login(now);
		return LoginResultVO.builder().jwtLoginToken(createLoginToken(socialMember)).isNew(isNew)
				.build();
	}

	private void oauth2TokenRevoke(UserSessionDto userDto, UnregisterRequestDTO dto) {
		log.info("Called tokenRevoke: {}", dto);
		if (dto == null) {
			throw NOT_FOUND_SOCIAL_INFO.toServiceException();
		}

		Oauth2LoginRequestVO requestVO = dto.toOauth2LoginRequestVO();

		if (dto.isAppleOauth()) {
			MemberToken memberToken = memberTokenRepository.findById(userDto.getUserId())
					.orElseThrow(NOT_FOUND_APPLE_TOKEN::toServiceException);
			requestVO = dto.toOauth2LoginRequestVO(memberToken.getToken());
		}

		oauth2Manager.revokeToken(requestVO);
	}

	@Override
	public void unregister(UserSessionDto userDto, UnregisterRequestDTO dto) {
		log.info("Called unregister: {}", dto);
		oauth2TokenRevoke(userDto, dto);
		memberService.deleteMember(userDto.getUserId(), dto.toOauth2LoginRequestVO(),
				LocalDateTime.now());

		// 탈퇴사유 전달 이벤트 발생
		eventPublisher.publishEvent(
				MemberUnregisterEvent
						.builder()
						.reason(dto.getUnregisterReasonDTO())
						.build());
	}

	private JwtLoginTokenDto createLoginToken(Member member) {
		return JwtLoginTokenDto.builder().accessToken(
				jwtTokenProvider.createCommonAccessToken(member.getId()).getTokenValue()).build();
	}

	private Optional<SocialMember> getSocialMember(Oauth2LoginInfoVO oauth2LoginInfo) {
		return memberQueryService.findSocialMemberByEmail(oauth2LoginInfo.getEmail());
	}

	private Optional<SocialMember> saveSocialMember(Oauth2LoginInfoVO oauth2LoginInfo) {
		log.debug("Called saveMember oauth2LoginInfo: {}", oauth2LoginInfo);
		try {
			return Optional.of(memberService.createSocialMember(oauth2LoginInfo));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
