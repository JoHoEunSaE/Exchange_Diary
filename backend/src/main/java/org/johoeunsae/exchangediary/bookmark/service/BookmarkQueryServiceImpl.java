package org.johoeunsae.exchangediary.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.dto.NotePreviewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteRelatedInfoDto;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.mapper.NoteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkQueryServiceImpl implements BookmarkQueryService {

	private final BookmarkRepository bookmarkRepository;
	private final NoteMapper noteMapper;
	private final LikeRepository likeRepository;

	@Override
	public NotePreviewPaginationDto getBookmarkList(Long loginMemberId, Long memberId, Pageable pageable) {
		// 노트와 관련된 정보를 받아옴
		// 노트, 멤버(작성자), 다이어리, 읽음 여부
		Page<NoteRelatedInfoDto> noteRelatedInfoList = bookmarkRepository.getBookmarkListByMemberId(loginMemberId,
				memberId, pageable);
		List<NotePreviewDto> list = noteRelatedInfoList.stream().map((n) -> noteMapper.toNotePreviewDto(n.getNote(),
		n.getDiaryInfo(), n.getMember(), n.isHasRead(), n.isBlocked(), likeRepository.countByNoteId(n.getNote().getId()))).collect(Collectors.toList());
		return noteMapper.toNotePreviewPaginationDto(noteRelatedInfoList.getTotalElements(), list);
	}

}
