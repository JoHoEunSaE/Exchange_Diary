package org.johoeunsae.exchangediary.notice.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.dto.NoticeDto;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoticeExceptionStatus;
import org.johoeunsae.exchangediary.mapper.NoticeMapper;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.notice.domain.Notice;
import org.johoeunsae.exchangediary.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	private final NoticeRepository noticeRepository;
	private final MemberRepository memberRepository;
	private final NoticeMapper noticeMapper;

	@Override
	@Transactional
	public void deleteAllByIds(Long loginUserId, List<Long> noticeIds) {
		Member loginUser = memberRepository.findById(loginUserId)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);

		List<Notice> notices = noticeRepository.findAllById(noticeIds);
		notices.stream().parallel()
				.filter(n -> !n.getReceiver().equals(loginUser))
				.findAny().ifPresent(n -> {
					throw NoticeExceptionStatus.NOT_BELONGED.toServiceException();
				});

		noticeRepository.deleteAll(notices);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NoticeDto> findAllByMemberId(Long memberId) {
		Member loginUser = memberRepository.findById(memberId)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);

		List<Notice> notices = noticeRepository.findAllByMemberId(loginUser.getId());
		return notices.stream()
				.map(noticeMapper::toNoticeDto)
				.collect(Collectors.toList());
	}
}
