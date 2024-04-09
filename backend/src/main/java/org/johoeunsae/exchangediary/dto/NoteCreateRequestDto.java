package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.ToString;
import org.johoeunsae.exchangediary.diary.validation.NoteCreateValidation;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.springframework.web.multipart.MultipartFile;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "NoteCreateDto", description = "일기 생성 정보")
@NoteCreateValidation
@ToString
public class NoteCreateRequestDto {

	@Schema(name = "title", description = "일기 제목", example = "제목")
	private final String title;
	@Schema(name = "content", description = "일기 내용", example = "내용")
	private final String content;
	@Schema(name = "imageUrls", description = "일기 이미지 경로", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private final List<String> imageUrls;
	@Schema(name = "visibleScope", description = "일기 공개 범위", example = "PUBLIC")
	private final VisibleScope visibleScope;
}
