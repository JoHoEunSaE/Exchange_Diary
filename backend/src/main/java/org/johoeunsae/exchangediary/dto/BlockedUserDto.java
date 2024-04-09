package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "차단한 멤버 정보")
@Builder
@AllArgsConstructor
@Getter
public class BlockedUserDto {
	@Schema(description = "차단한 멤버의 고유 id", example = "1")
	private final Long blockedUserId;
	@Schema(description = "차단한 멤버의 닉네임", example = "johoeunsae")
	private final String nickname;
	@Schema(description = "차단한 멤버의 프로필 이미지 url", example = "https://exchangediary.s3.ap-northeast-2.amazonaws.com/profile/1.png")
	private final String profileImageUrl;
}
