package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.johoeunsae.exchangediary.diary.validation.NoteUpdateValidation;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
@FieldNameConstants
@NoteUpdateValidation
@Schema(name = "NoteUpdateDto", description = "일기 업데이트 정보")
public class NoteUpdateDto {
	@Schema(name = "title", description = "일기 제목", example = "제목")
	private String title;
	@Schema(name = "content", description = "일기 내용", example = "내용")
	private String content;
}
