package org.johoeunsae.exchangediary.blacklist.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.blacklist.domain.Blacklist;
import org.johoeunsae.exchangediary.blacklist.repository.BlacklistRepository;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.utils.DateUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

	private final MemberRepository memberRepository;
	private final BlacklistRepository blacklistRepository;

	// 외부 스케줄링으로 처리하고 있으므로 이 메서드는 사용되지 않는다.
	@Override
	public Blacklist addBlacklist(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);

		// 블랙리스트 유저 처리
		member.updateRole(MemberRole.BLACKLIST_USER);
		memberRepository.save(member);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endedAt = DateUtil.getInfinityDate();

		Blacklist blacklist = Blacklist.of(member, now, endedAt);
		return blacklistRepository.save(blacklist);
	}
}
