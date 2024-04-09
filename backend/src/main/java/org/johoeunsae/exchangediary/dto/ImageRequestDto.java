package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Presigned-url 발급을 위한 정보 요청")
public class ImageRequestDto {

	@Schema(description = "s3에 저장될 파일 경로(ObjectKey)", example = "profile-images/random-string/diary.jpg")
	private String imageUrl;

	@Builder
	ImageRequestDto(final String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
