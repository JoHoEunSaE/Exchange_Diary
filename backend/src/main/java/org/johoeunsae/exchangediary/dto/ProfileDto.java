package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ProfileDto {

	@Schema(description = "멤버 ID", example = "1")
	private final Long memberId;
	@Schema(description = "멤버 닉네임", example = "johoeunsae")
	private final String nickname;
	@Schema(description = "멤버 이메일", example = "johoeunsae@naver.com")
	private final String email;
	@Schema(description = "멤버 상태메시지", example = "조훈세")
	private final String statement;
	@Schema(description = "멤버 프로필 이미지 URL", example = "http://localhost:8080/v1/members/johoeunsae/profile/image")
	private final String profileImageUrl;
	@Schema(description = "팔로워 수", example = "10")
	private final Integer followerCount;
	@Schema(description = "팔로잉 수", example = "10")
	private final Integer followingCount;
	@Schema(description = "팔로잉 여부", example = "true")
	private final Boolean isFollowing;
	@Schema(description = "차단 여부", example = "false")
	private final Boolean isBlocked;
}
