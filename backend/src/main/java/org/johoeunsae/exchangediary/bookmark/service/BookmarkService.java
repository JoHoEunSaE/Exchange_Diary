package org.johoeunsae.exchangediary.bookmark.service;

import java.util.List;
import org.johoeunsae.exchangediary.dto.NotePreviewDto;

public interface BookmarkService {
	void createBookmark(Long memberId, Long noteId);
	void deleteBookmark(Long memberId, Long noteId);
}
