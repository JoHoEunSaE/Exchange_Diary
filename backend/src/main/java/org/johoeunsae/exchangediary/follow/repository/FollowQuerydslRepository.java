package org.johoeunsae.exchangediary.follow.repository;

import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.follow.repository.dto.MemberPrivacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowQuerydslRepository {

	Page<MemberPrivacy> findFollowerPrivacyList(Long memberId, Pageable pageable);

	Page<MemberPrivacy> findFollowingPrivacyList(Long memberId, Pageable pageable);

	Page<MemberPreviewDto> findFollowerPrivacyListForLogin(Long loginId, Long memberId,
			Pageable pageable);

	Page<MemberPreviewDto> findFollowingPrivacyListForLogin(Long loginId, Long memberId,
			Pageable pageable);

	boolean existsTargetMemberIdByMemberId(Long fromMemberId, Long toMemberId);
}
