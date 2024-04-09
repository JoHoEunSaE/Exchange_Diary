package org.johoeunsae.exchangediary.notice.service;

import org.johoeunsae.exchangediary.dto.NoticeDeleteRequestDto;
import org.johoeunsae.exchangediary.dto.NoticeDto;

import java.util.List;

public interface NoticeFacadeService {

	void deleteNotices(Long loginUserId, NoticeDeleteRequestDto requestDto);

	List<NoticeDto> getAllNotices(Long loginUserId);
}
