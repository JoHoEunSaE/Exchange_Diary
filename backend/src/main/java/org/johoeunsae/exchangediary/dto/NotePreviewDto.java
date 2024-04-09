package org.johoeunsae.exchangediary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;
import org.johoeunsae.exchangediary.utils.obfuscation.DecodeSerializer;

@AllArgsConstructor
@Getter
@Schema(description = "일기 미리보기")
@Decodable
public class NotePreviewDto implements ImageHolder {

	@Schema(description = "일기 ID", example = "1")
	private final Long noteId;
	@Schema(description = "일기장 ID", example = "1")
	private final Long diaryId;
	@Schema(description = "일기 작성자 정보", implementation = AuthorDto.class)
	private final AuthorDto author;
	@Schema(description = "일기 미리보기", example = "오늘은 날씨가 좋다.")
	private final String preview;
	@Schema(description = "일기 그룹명", example = "날씨원정대")
	private final String groupName;
	@Schema(description = "일기 제목", example = "오늘의 날씨")
	@JsonSerialize(using = DecodeSerializer.class)
	private final String title;
	@Schema(description = "일기 썸네일 URL", example = "[이미지 주소]")
	private String thumbnailUrl;
	@Schema(description = "일기 생성일", example = "2023-06-03T00:00:00")
	private final LocalDateTime createdAt;
	@Schema(description = "일기 수정일", example = "PUBLIC")
	private final VisibleScope visibleScope;
	@Schema(description = "일기 읽음 여부", example = "true")
	private final boolean hasRead;
	@Schema(description = "일기 작성자 차단 여부", example = "false")
	@Getter(onMethod_ = @JsonGetter("isBlocked"))
	private final boolean isBlocked;
	@Schema(description = "일기 좋아요 수", example = "10")
	private final Integer likeCount;

	@Override
	public void setImageUrl(String imageUrl) {
		this.thumbnailUrl = imageUrl;
	}
}
