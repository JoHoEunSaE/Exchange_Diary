package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter @Setter
@ToString
public class MemberUpdateDto {
	@Schema(description = "회원 ID", example = "1")
	private Long memberId;
	@Schema(description = "닉네임", example = "닉네임")
	private String nickname;
	@Schema(description = "한 줄 소개", example = "Hello world!")
	private String statement;
	@Schema(description = "프로필 이미지", example = "https://s3")
	private String profileImageUrl;
}
