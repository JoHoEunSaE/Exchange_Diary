package org.johoeunsae.exchangediary.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "MemberPreviewDto", description = "회원 미리보기")
@EqualsAndHashCode
public class DiaryMemberPreviewDto {
	@Schema(name = "memberId", description = "회원 id", example = "1")
	private final Long memberId;
	@Schema(name = "nickname", description = "닉네임", example = "동글동글동그리")
	private final String nickname;
	@Schema(name = "profileImageUrl", description = "프로필 이미지 url", example = "[이미지 주소]")
	private final String profileImageUrl;
	@Schema(name = "isFollowing", description = "내가 팔로잉하는지 여부", example = "true")
	@Getter(onMethod_ = @JsonGetter("isFollowing"))
	private final boolean isFollowing;
	@Schema(name = "isMaster", description = "다이어리 마스터 여부", example = "true")
	@Getter(onMethod_ = @JsonGetter("isMaster"))
	private final boolean isMaster;
	@Schema(name = "isBlocked", description = "차단 여부", example = "true")
	@Getter(onMethod_ = @JsonGetter("isBlocked"))
	private final boolean isBlocked;
}
