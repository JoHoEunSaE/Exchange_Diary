package org.johoeunsae.exchangediary.notice.service;

import org.johoeunsae.exchangediary.dto.NoticeDto;

import java.util.List;

public interface NoticeService {

	void deleteAllByIds(Long loginUserId, List<Long> noticeIds);

	List<NoticeDto> findAllByMemberId(Long memberId);
}
