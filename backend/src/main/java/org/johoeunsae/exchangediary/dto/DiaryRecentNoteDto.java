package org.johoeunsae.exchangediary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;
import org.johoeunsae.exchangediary.utils.obfuscation.DecodeSerializer;

// Diary ID
// Diary name
// Recent Note name
// Recent Note preview (first 50 characters)
// Recent Note Author
@NoArgsConstructor
@Getter
@Schema(name = "DiaryRecentNoteDto", description = "다이어리의 최근 일기 정보")
@Decodable
public class DiaryRecentNoteDto {

	@Schema(description = "다이어리 ID", example = "99")
	private Long diaryId;
	@Schema(description = "일기 제목", example = "조은사이")
	private String diaryTitle;
	@Schema(description = "일기 그룹명", example = "날씨원정대")
	private String groupName;

	@Schema(description = "일기 ID", example = "99")
	private Long noteId;
	@Schema(description = "일기 작성자 정보", implementation = AuthorDto.class)
	private AuthorDto author;
	@Schema(description = "일기 미리보기", example = "오늘은 날씨가 좋다.")
	private String preview;
	@Schema(description = "일기 제목", example = "오늘의 날씨")
	@JsonSerialize(using = DecodeSerializer.class)
	private String title;
	@Schema(description = "일기 썸네일 URL", example = "[이미지 주소]")
	private String thumbnailUrl;
	@Schema(description = "일기 생성일", example = "2023-06-03T00:00:00")
	private LocalDateTime createdAt;
	@Schema(description = "일기 공개여부", example = "PUBLIC")
	private VisibleScope visibleScope;
	@Schema(description = "일기 작성자 차단 여부", example = "false")
	@Getter(onMethod_ = @JsonGetter("isBlocked"))
	private boolean isBlocked;


	@Builder
	public DiaryRecentNoteDto(DiaryNoteMemberDto dto, String profileImageUrl, String thumbnailUrl) {
		this.diaryId = dto.getDiaryId();
		this.diaryTitle = dto.getDiaryTitle();
		this.groupName = dto.getGroupName();
		this.noteId = dto.getNote().getId();
		this.author = AuthorDto.builder()
				.memberId(dto.getMember().getId())
				.nickname(dto.getMember().getNickname())
				.profileImageUrl(profileImageUrl)
				.build();
		this.preview = dto.getNote().getPreview();
		this.title = dto.getNote().getTitle();
		this.thumbnailUrl = thumbnailUrl;
		this.createdAt = dto.getNote().getCreatedAt();
		this.visibleScope = dto.getNote().getVisibleScope();
		this.isBlocked = dto.isBlocked();
	}
}
