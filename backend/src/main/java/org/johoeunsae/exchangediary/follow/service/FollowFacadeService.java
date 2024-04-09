package org.johoeunsae.exchangediary.follow.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.springframework.data.domain.Pageable;

public interface FollowFacadeService {

	MemberPreviewPaginationDto getFollowers(Optional<Long> loginId, Long targetId,
			Pageable pageable);

	MemberPreviewPaginationDto getFollowings(Optional<Long> loginId, Long memberId,
			Pageable pageable);

	void deleteFollow(Long userId, Long memberId);

	void createFollow(Long userId, Long memberId, LocalDateTime now);
}
