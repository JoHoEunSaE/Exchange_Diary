package org.johoeunsae.exchangediary.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.dto.MemberUpdateRequestDto;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.image.domain.ImageDeleteEvent;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.member.domain.*;
import org.johoeunsae.exchangediary.member.domain.extension.MemberPass;
import org.johoeunsae.exchangediary.member.domain.policy.MemberPolicy;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.member.repository.MemberTokenRepository;
import org.johoeunsae.exchangediary.member.service.preprocessor.MemberPreprocessor;
import org.johoeunsae.exchangediary.notice.domain.DeviceRegistry;
import org.johoeunsae.exchangediary.notice.repository.DeviceRegistryRepository;
import org.johoeunsae.exchangediary.utils.update.UpdateException;
import org.johoeunsae.exchangediary.utils.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus.INCORRECT_ARGUMENT;
import static org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus.INTERNAL_SERVER_ERROR;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService {

	private final ImageService imageService;
	private final MemberPolicy memberPolicy;
	private final DeviceRegistryRepository deviceRegistryRepository;
	private final MemberRepository memberRepository;
	private final MemberPreprocessor memberPreprocessor;
	private final MemberTokenRepository memberTokenRepository;
	private final ApplicationEventPublisher eventPublisher;
	@Value("${spring.images.path.profile}")
	public String PROFILE_IMAGE_DIR;

	@Override
	public SocialMember createSocialMember(Oauth2LoginInfoVO vo) {
		log.info("createSocialMember: vo={}", vo);

		LocalDateTime now = LocalDateTime.now();
		MemberFeatures identity = MemberFeatures.of(vo.getEmail(),
				memberPolicy.createRandomNickname());
		OauthInfo oauthInfo = OauthInfo.of(vo.getOauthId(), vo.getOauthType());

		SocialMember member = Member.createSocialMember(identity, now, oauthInfo);
		if (vo.getRefreshToken().isPresent()) {
			MemberToken token = MemberToken.of(member, vo.getRefreshToken().get(), now);
			memberTokenRepository.save(token);
		}
		member = memberRepository.save(member);
		return member;
	}

	@Override
	public void updateProfile(Long memberId,
	                                     MemberUpdateRequestDto memberUpdateRequestDto) {
		log.info("updateProfile: memberId={}, memberUpdateRequestDto={}", memberId,
				memberUpdateRequestDto);

		Member member = memberRepository.findById(memberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

		// 닉네임 변경
		updateNicknameIfPossible(member, memberUpdateRequestDto.getNickname());
		// 한 줄 소개 변경
		member.updateStatement(memberUpdateRequestDto.getStatement());
		// 프로필 이미지 변경
		updateProfileImageIfPossible(member, memberUpdateRequestDto.getProfileImageUrl());

		memberRepository.save(member);
	}

	private void checkNicknameDuplicate(Long memberId, String nickname) {
		memberRepository.findByNickname(nickname)
				.ifPresent(m -> {
					if (!memberId.equals(m.getId())) {
						throw new ServiceException(MemberExceptionStatus.DUPLICATE_NICKNAME);
					}
				});
	}

	@Override
	public void deleteProfileImage(Long memberId) {
		log.info("deleteProfileImage: memberId={}", memberId);
		Member member = memberRepository.findById(memberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		String existingProfileFilename = member.getProfileImageUrl();
		if (Objects.nonNull(existingProfileFilename) && !existingProfileFilename.isBlank()) {
			eventPublisher.publishEvent(new ImageDeleteEvent(existingProfileFilename));
		}
		member.updateProfileImageUrl(null);
		memberRepository.save(member);
	}


	@Override
	public void deleteMember(Long memberId, Oauth2LoginRequestVO dto, LocalDateTime now) {
		log.info("delete Member memberId={}", memberId);
		Member member = memberRepository.findMemberByDeletedAtIsNotNullAndId(memberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		memberPreprocessor.delete(MemberPass.builder().member(member).loginDto(dto).build());
		member.delete(now);
	}

	@Override
	/**
	 * 상한선까지 디바이스 토큰을 저장합니다.
	 * 상한선을 초과하게 되는 경우, 가장 오래된 디바이스 토큰을 삭제합니다.
	 */
	public void upsertDeviceRegistry(Member member, String deviceToken, LocalDateTime now) {
		log.info("addDeviceRegistry: member={}, deviceToken={}", member, deviceToken);
		List<DeviceRegistry> registries = deviceRegistryRepository
				.findByMemberIdOrderByCreatedAt(member.getId());
		if (registries.stream().anyMatch(r -> r.getToken().equals(deviceToken)))
			return;
		DeviceRegistry registry = DeviceRegistry.of(member, deviceToken, now);
		if (registries.size() >= DeviceRegistry.MAX_DEVICE_COUNT) {
			DeviceRegistry oldest = registries.get(0);
			deviceRegistryRepository.delete(oldest);
		}
		deviceRegistryRepository.save(registry);
	}

	@Deprecated
	private void validateRequest(UpdateRequest<Member> request) {
		if (request.isValidated()) {
			return;
		}
		try {
			request.validate();
		} catch (UpdateException e) {
			switch (e.getStatus()) {
				case IllegalState:
					throw INTERNAL_SERVER_ERROR.toServiceException();
				case IllegalArgument:
					throw new ServiceException(HttpStatus.BAD_REQUEST, e.getMessage());
				default:
					log.warn("UpdateException {}이 관리되지 않습니다.", e.getStatus());
					throw INCORRECT_ARGUMENT.toServiceException();
			}
		}
	}

	private void deletePreviousProfileImage(Member member) {
		String existingProfileFilename = member.getProfileImageUrl();
		if (Objects.nonNull(existingProfileFilename) && !existingProfileFilename.isBlank()) {
			eventPublisher.publishEvent(new ImageDeleteEvent(existingProfileFilename));
		}
	}

	private void updateNicknameIfPossible(Member member, String nickname) {
		// 이전과 같은 닉네임이면 업데이트 하지 않음
		if (member.isEqualNickname(nickname)) {
			return;
		}
		// 중복 확인
		checkNicknameDuplicate(member.getId(), nickname);
		// 변경 날짜 확인
		if (!memberPolicy.isUpdatableNicknameDate(member.getNicknameUpdatedAt(),
				LocalDateTime.now())) {
			throw new ServiceException(MemberExceptionStatus.NOT_POSSIBLE_PERIOD);
		}
		member.updateNickname(nickname, LocalDateTime.now());
	}

	private void updateProfileImageIfPossible(Member member, String profileImageUrl) {
		// request로 들어온 프로필 이미지 url
		// null이면 기본 이미지로 변경
		// null이 아니면 기존 이미지 삭제 후 변경
		if (Objects.isNull(profileImageUrl) || profileImageUrl.isBlank()) {
			deletePreviousProfileImage(member);
			member.changeDefaultProfileImage();
		} else {
			String imageUrl = imageService.parseImageUrl(profileImageUrl, PROFILE_IMAGE_DIR);
			log.info("imageUrl: {}", imageUrl);
			// 기존 프로필 이미지와 같다면 업데이트 하지 않음
			if (member.isEqualProfileImageUrl(imageUrl)) {
				return;
			}
			// 기존 프로필 이미지 삭제
			deletePreviousProfileImage(member);
			member.updateProfileImageUrl(imageUrl);
		}
	}
}
