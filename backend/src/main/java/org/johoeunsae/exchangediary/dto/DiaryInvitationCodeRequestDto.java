package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "일기장 가입을 위한 초대 코드 Request Dto")
public class DiaryInvitationCodeRequestDto {

	@Schema(description = "일기장 초대 코드", example = "R43QTP")
	@NotNull
	private String code;

	@Builder
	DiaryInvitationCodeRequestDto(final String code) {
		this.code = code;
	}
}
