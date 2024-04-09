package org.johoeunsae.exchangediary.follow.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.mapper.MemberMapper;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.service.MemberQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Log4j2
public class FollowFacadeServiceImpl implements FollowFacadeService {

	private final FollowService followService;
	private final FollowQueryService followQueryService;
	private final MemberQueryService memberQueryService;
	private final MemberMapper memberMapper;

	@Override
	public MemberPreviewPaginationDto getFollowers(Optional<Long> loginId, Long targetId,
			Pageable pageable) {
		log.info("loginId: {}, targetId: {}, pageable: {}", loginId, targetId, pageable);
		Member target = memberQueryService.getMember(targetId);
		Page<MemberPreviewDto> memberPreviewDtos = loginId
				.map(id -> followQueryService.getFollowersForLogin(id, target, pageable))
				.orElseGet(() -> followQueryService.getFollowers(target, pageable));
		return memberMapper.toMemberPreviewPaginationDto(memberPreviewDtos.toList(),
				memberPreviewDtos.getTotalElements());
	}

	@Override
	public MemberPreviewPaginationDto getFollowings(Optional<Long> loginId, Long memberId,
			Pageable pageable) {
		log.info("loginId: {}, memberId: {}, pageable: {}", loginId, memberId, pageable);
		Member target = memberQueryService.getMember(memberId);
		Page<MemberPreviewDto> memberPreviewDtos = loginId
				.map(id -> followQueryService.getFollowingsForLogin(id, target, pageable))
				.orElseGet(() -> followQueryService.getFollowings(target, pageable));
		return memberMapper.toMemberPreviewPaginationDto(memberPreviewDtos.toList(),
				memberPreviewDtos.getTotalElements());
	}

	@Override
	public void deleteFollow(Long userId, Long memberId) {
		log.info("userId: {}, memberId: {}", userId, memberId);
		followService.deleteFollow(userId, memberId);
	}

	@Override
	public void createFollow(Long userId, Long memberId, LocalDateTime now) {
		log.info("userId: {}, memberId: {}", userId, memberId);
		Member from = memberQueryService.getMember(userId);
		Member to = memberQueryService.getMember(memberId);
		followService.createFollow(from, to, now);
	}
}
