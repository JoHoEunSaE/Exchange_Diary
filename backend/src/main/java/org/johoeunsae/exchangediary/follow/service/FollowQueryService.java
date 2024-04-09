package org.johoeunsae.exchangediary.follow.service;

import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowQueryService {

	boolean isFollowing(Long loginMemberId, Long targetId);

	boolean isFollower(Long loginMemberId, Long targetId);

	Page<MemberPreviewDto> getFollowersForLogin(Long loginMemberId, Member target,
			Pageable pageable);

	Page<MemberPreviewDto> getFollowers(Member target, Pageable pageable);

	Page<MemberPreviewDto> getFollowingsForLogin(Long loginMemberId, Member target,
			Pageable pageable);

	Page<MemberPreviewDto> getFollowings(Member target, Pageable pageable);
}
