package org.johoeunsae.exchangediary.follow.service;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.exception.status.FollowExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.follow.domain.Follow;
import org.johoeunsae.exchangediary.follow.repository.FollowRepository;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class FollowServiceImpl implements FollowService {
	private final FollowRepository followRepository;

	@Override
	public void deleteFollow(Long userId, Long memberId) {
		log.info("deleteFollow userId: {}, memberId: {}", userId, memberId);
		followRepository.findById(MemberCompositeKey.of(userId, memberId))
				.ifPresentOrElse(this::deleteIfPresent, this::throwIfNotPresent);
	}

	@Override
	public void createFollow(Member from, Member to, LocalDateTime now) {
		log.info("createFollow from: {}, to: {}", from, to);
		MemberFromTo followKey = MemberFromTo.of(from, to);

		verifyFollow(followKey);
		followRepository.save(Follow.of(followKey, now));
	}

	private void deleteIfPresent(Follow follow) {
		log.info("deleteIfPresent follow: {}", follow);
		followRepository.delete(follow);
	}

	private void throwIfNotPresent() {
		log.info("throwIfNotPresent");
		throw MemberExceptionStatus.NOT_FOUND_MEMBER.toServiceException();
	}

	private void verifyFollow(MemberFromTo followPair) {
		Long ownerId = followPair.getFrom().getId();
		Long possessionId = followPair.getTo().getId();

		if (Objects.equals(ownerId, possessionId))
			throw FollowExceptionStatus.SELF_FOLLOW.toServiceException();
		if (followRepository.existsById(followPair.asKey()))
			throw FollowExceptionStatus.DOUBLE_FOLLOW.toServiceException();
	}
}
