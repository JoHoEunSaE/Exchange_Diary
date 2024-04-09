package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "NoteImageCreateDto", description = "일기 이미지 생성 정보")
public class NoteImageCreateDto {
	@Schema(name = "imageIndex", description = "일기 이미지 인덱스", example = "0")
	private final Integer imageIndex;

	@Schema(name = "imageUrl", description = "s3에 저장된 일기 이미지 경로", example = "images/random-string/image.jpg")
	private final String imageUrl;
}
