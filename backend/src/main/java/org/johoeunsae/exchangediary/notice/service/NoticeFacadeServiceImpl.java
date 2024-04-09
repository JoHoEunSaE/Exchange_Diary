package org.johoeunsae.exchangediary.notice.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.dto.NoticeDeleteRequestDto;
import org.johoeunsae.exchangediary.dto.NoticeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeFacadeServiceImpl implements NoticeFacadeService {

	private final NoticeService noticeService;

	@Override
	public void deleteNotices(Long loginUserId, NoticeDeleteRequestDto noticeDeleteRequestDto) {
		noticeService.deleteAllByIds(loginUserId, noticeDeleteRequestDto.getNoticeIds());
	}

	@Override
	@Transactional(readOnly = true)
	public List<NoticeDto> getAllNotices(Long loginUserId) {
		return noticeService.findAllByMemberId(loginUserId);
	}
}
