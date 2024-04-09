package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "방장 변경 요청")
@AllArgsConstructor
public class DiaryMasterUpdateRequestDto {
	private final Long masterId;
}
