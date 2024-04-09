package org.johoeunsae.exchangediary.follow.service;

import java.time.LocalDateTime;
import org.johoeunsae.exchangediary.member.domain.Member;

public interface FollowService {
	void deleteFollow(Long userId, Long memberId);
	void createFollow(Member from, Member to, LocalDateTime now);
}
