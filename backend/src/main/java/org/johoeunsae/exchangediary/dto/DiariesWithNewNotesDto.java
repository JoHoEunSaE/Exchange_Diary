package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "DiariesWithNewNotesDto", description = "일기장 목록과 새로운 일기 목록")
public class DiariesWithNewNotesDto {

	@ArraySchema(schema = @Schema(implementation = DiaryPreviewDto.class))
	private final List<DiaryPreviewDto> diaryPreviewDtoList;
	@ArraySchema(schema = @Schema(implementation = NotePreviewDto.class))
	private final List<NotePreviewDto> notePreviewDtoList;
}
