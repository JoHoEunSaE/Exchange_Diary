package org.johoeunsae.exchangediary.block.service;

import java.util.Set;
import org.johoeunsae.exchangediary.dto.BlockedUserPaginationDto;
import org.springframework.data.domain.Pageable;

public interface BlockQueryService {

	BlockedUserPaginationDto getBlockedUsers(Long loginMemberId, Pageable pageable);

	Set<Long> getBlockedMemberIds(Long loginMemberId);

	boolean isBlocked(Long loginMemberId, Long targetMemberId);
}
