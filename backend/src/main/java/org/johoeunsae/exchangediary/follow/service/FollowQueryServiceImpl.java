package org.johoeunsae.exchangediary.follow.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.follow.repository.FollowQuerydslRepository;
import org.johoeunsae.exchangediary.follow.repository.dto.MemberPrivacy;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowQueryServiceImpl implements FollowQueryService {

	private final FollowQuerydslRepository followQuerydslRepository;

	@Override
	public Page<MemberPreviewDto> getFollowersForLogin(Long loginMemberId, Member target,
			Pageable pageable) {
		return followQuerydslRepository.findFollowerPrivacyListForLogin(loginMemberId,
				target.getId(), pageable);
	}

	@Override
	public Page<MemberPreviewDto> getFollowers(Member target, Pageable pageable) {
		return followQuerydslRepository.findFollowerPrivacyList(
						target.getId(), pageable)
				.map(this::toMemberPreviewDto);
	}

	@Override
	public Page<MemberPreviewDto> getFollowingsForLogin(Long loginMemberId, Member target,
			Pageable pageable) {
		return followQuerydslRepository.findFollowingPrivacyListForLogin(loginMemberId,
				target.getId(), pageable);
	}

	@Override
	public Page<MemberPreviewDto> getFollowings(Member target, Pageable pageable) {
		return followQuerydslRepository.findFollowingPrivacyList(target.getId(), pageable)
				.map(this::toMemberPreviewDto);
	}


	private MemberPreviewDto toMemberPreviewDto(MemberPrivacy privacy) {
		return MemberPreviewDto.builder()
				.memberId(privacy.getMemberId())
				.nickname(privacy.getNickname())
				.profileImageUrl(privacy.getProfileImageUrl())
				.isFollowing(false)
				.build();
	}

	@Override
	public boolean isFollowing(Long loginMemberId, Long targetId) {
		return followQuerydslRepository.existsTargetMemberIdByMemberId(loginMemberId, targetId);
	}

	@Override
	public boolean isFollower(Long loginMemberId, Long targetId) {
		return followQuerydslRepository.existsTargetMemberIdByMemberId(targetId, loginMemberId);
	}
}
