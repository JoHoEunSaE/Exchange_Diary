package org.johoeunsae.exchangediary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;
import org.johoeunsae.exchangediary.utils.obfuscation.DecodeSerializer;

@Builder
@Getter
@Schema(description = "일기장의 일기 상세 조회 시 반환되는 Dto")
@Decodable
public class DiaryNoteViewDto {

	@Schema(description = "일기 id", example = "1")
	private final Long noteId;

	@Schema(description = "일기장 ID", example = "1")
	private final Long diaryId;

	@Schema(description = "일기 제목", example = "삶과 죽음에 관하여...")
	@JsonSerialize(using = DecodeSerializer.class)
	private final String title;

	@Schema(description = "일기 내용", example = "삶은 계란이다. 죽음은 치킨이다.")
	@JsonSerialize(using = DecodeSerializer.class)
	private final String content;

	@Schema(description = "일기 작성자", implementation = AuthorDto.class)
	private final AuthorDto author;

	@ArraySchema(schema = @Schema(description = "일기 이미지 목록", implementation = NoteImageDto.class))
	private final List<NoteImageDto> imageList;

	@Schema(description = "일기 생성일", example = "2021-08-01T00:00:00")
	private final LocalDateTime createdAt;

	@Schema(description = "일기 수정일", example = "2021-08-01T00:00:00")
	private final LocalDateTime updatedAt;

	@Schema(description = "일기 북마크 여부", example = "true")
	private final boolean isBookmarked;
	@Schema(description = "일기 좋아요 여부", example = "true")
	private final boolean isLiked;
	@Schema(description = "일기 좋아요 수", example = "1")
	private final Integer likeCount;
	@Schema(description = "일기 공개범위", example = "PUBLIC")
	private final VisibleScope visibleScope;
	@Schema(description = "다음 일기 id", example = "2")
	private final Long nextNoteId;
	@Schema(description = "이전 일기 id", example = "0")
	private final Long prevNoteId;
	@Schema(description = "일기 작성자 차단 여부", example = "false")
	@Getter(onMethod_ = @JsonGetter("isBlocked"))
	private boolean isBlocked;

	@JsonGetter("isBookmarked")
	public boolean isBookmarked() {
		return isBookmarked;
	}

	@JsonGetter("isLiked")
	public boolean isLiked() {
		return isLiked;
	}
}
