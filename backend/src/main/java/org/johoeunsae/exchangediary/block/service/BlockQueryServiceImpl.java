package org.johoeunsae.exchangediary.block.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.block.domain.Block;
import org.johoeunsae.exchangediary.block.repository.BlockRepository;
import org.johoeunsae.exchangediary.dto.BlockedUserPaginationDto;
import org.johoeunsae.exchangediary.mapper.MemberMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryServiceImpl implements BlockQueryService {

	private final BlockRepository blockRepository;
	private final MemberMapper memberMapper;

	@Override
	public BlockedUserPaginationDto getBlockedUsers(Long loginMemberId, Pageable pageable) {
		Page<Block> blocks = blockRepository.findAllByMemberId(loginMemberId, pageable);
		return BlockedUserPaginationDto.builder()
				.result(blocks.stream().map(
						block -> memberMapper.toBlockedUserDto(block.getTo())
				).collect(Collectors.toList()))
				.totalLength(blocks.getTotalElements())
				.build();
	}

	@Override
	public Set<Long> getBlockedMemberIds(Long loginMemberId) {
		return blockRepository.findAllByMemberId(loginMemberId).stream()
				.map(block -> block.getTo().getId())
				.collect(Collectors.toSet());
  }

	@Override
	public boolean isBlocked(Long loginMemberId, Long targetMemberId) {
		return blockRepository.existsByMemberIdAndTargetMemberId(loginMemberId, targetMemberId);
	}

}
