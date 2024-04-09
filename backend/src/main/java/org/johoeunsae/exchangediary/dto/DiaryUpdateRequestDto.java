package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.validation.DiaryUpdateValidation;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "일기장 수정 요청")
@DiaryUpdateValidation
@ToString
public class DiaryUpdateRequestDto {
	@Schema(description = "일기장 커버 데이터 (이미지 Url or 컬러코드)", example = "#000000FF", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@NotNull
	private final String coverData;

	@Schema(description = "일기장 커버 타입", example = "COLOR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@NotNull
	private final CoverType coverType;

	@Schema(description = "일기장 제목", example = "이것은 일기장 제목", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@NotBlank
	private final String title;

	@Schema(description = "일기장 그룹 이름", example = "이것은 그룹 이름", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private final String groupName;
}
