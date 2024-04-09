package org.johoeunsae.exchangediary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;

@Builder
@AllArgsConstructor
@Getter
public class NoteLinkedViewDto {

	private final Long noteId;
	private final Long diaryId;
	private final String content;
	private final AuthorDto author;
	private final String title;
	private final List<NoteImageDto> imageList;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
	private final boolean isBookmarked;

	@JsonGetter("isBookmarked")
	public boolean isBookmarked() {
		return isBookmarked;
	}

	private final boolean isLiked;

	@JsonGetter("isLiked")
	public boolean isLiked() {
		return isLiked;
	}

	private final Integer likeCount;
	private final Long nextNoteId;
	private final Long prevNoteId;
	private final VisibleScope visibleScope;
}
