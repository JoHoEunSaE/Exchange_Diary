package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.johoeunsae.exchangediary.member.validation.NicknameValidation;
import org.johoeunsae.exchangediary.member.validation.StatementValidation;

@Builder
@AllArgsConstructor
@Getter @Setter
@ToString
public class MemberUpdateRequestDto {

	@Schema(description = "닉네임을 설정", example = "닉네임", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@NicknameValidation
	@NotNull
	private String nickname;
	@Schema(description = "한 줄 소개", example = "Hello world!", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@StatementValidation
	@NotNull
	private String statement;
	@Schema(description = "s3 url", example = "profile-images/random-string/image.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String profileImageUrl;
}
