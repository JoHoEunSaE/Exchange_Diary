package org.johoeunsae.exchangediary.block.service;

import static org.johoeunsae.exchangediary.exception.status.BlockExceptionStatus.ALREADY_BLOCKED_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus.UNAUTHENTICATED;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.BlockExceptionStatus.NOT_FOUND_BLOCK;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.block.domain.Block;
import org.johoeunsae.exchangediary.block.repository.BlockRepository;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BlockServiceImpl implements BlockService {

	private final MemberRepository memberRepository;
	private final BlockRepository blockRepository;

	@Override
	public void blockUser(Long loginMemberId, Long targetMemberId) {
		if (loginMemberId.equals(targetMemberId)) {
			throw new ServiceException(UNAUTHENTICATED);
		}
		if (blockRepository.existsById(MemberCompositeKey.of(loginMemberId, targetMemberId))) {
			throw new ServiceException(ALREADY_BLOCKED_MEMBER);
		}
		Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		Member targetMember = memberRepository.findById(targetMemberId).orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		log.info("{} blocks {}", loginMember.getNickname(), targetMember.getNickname());

		Block block = Block.of(MemberFromTo.of(loginMember, targetMember), LocalDateTime.now());
		blockRepository.save(block);
	}

	@Override
	public void unblockUser(Long loginMemberId, Long targetMemberId) {
		log.info("loginMemberId : {} unblocks targetMemberId : {}", loginMemberId, targetMemberId);
		Block block = blockRepository.findById(MemberCompositeKey.of(loginMemberId, targetMemberId))
				.orElseThrow(NOT_FOUND_BLOCK::toServiceException);
		blockRepository.delete(block);
	}
}
