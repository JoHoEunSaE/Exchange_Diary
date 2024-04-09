package org.johoeunsae.exchangediary.bookmark.service;

import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.springframework.data.domain.Pageable;

public interface BookmarkQueryService {

	/**
	 * 특정 멤버의 북마크 목록을 반환합니다.
	 *
	 * @param loginMemberId 로그인한 회원 ID
	 * @param memberId     조회할 회원 ID
	 * @param pageable  페이지네이션
	 * @return 북마크 목록
	 */
	NotePreviewPaginationDto getBookmarkList(Long loginMemberId, Long memberId, Pageable pageable);
}
