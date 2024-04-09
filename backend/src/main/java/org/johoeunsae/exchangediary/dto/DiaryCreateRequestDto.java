package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.validation.DiaryCoverCreateValidation;
import org.johoeunsae.exchangediary.diary.validation.DiaryTitleValidation;
import org.johoeunsae.exchangediary.diary.validation.GroupNameValidation;
import org.johoeunsae.exchangediary.message.ValidationMessage;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "일기장 생성 요청")
@DiaryCoverCreateValidation
@ToString
public class DiaryCreateRequestDto {

	@Schema(description = "일기장 커버 데이터 (이미지 Url, 컬러코드)", example = "#000000FF")
	private final String coverData;

	@Schema(description = "일기장 커버 타입", example = "COLOR")
	private final CoverType coverType;

	@Schema(description = "일기장 제목", example = "이것은 일기장 제목")
	@NotBlank(message = ValidationMessage.NOT_BLANK)
	@DiaryTitleValidation
	private final String title;

	@Schema(description = "일기장 그룹 이름", example = "이것은 그룹 이름")
	@GroupNameValidation
	private final String groupName;
}
