package org.johoeunsae.exchangediary.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter @ToString
@NoArgsConstructor
@Schema(description = "일기장 작성자")
public class AuthorDto {

	@Schema(description = "회원 ID", example = "1")
	private Long memberId;
	@Schema(description = "회원 닉네임", example = "동글동글동그리")
	private String nickname;
	@Schema(description = "회원 프로필 이미지 URL", example = "[이미지 주소]")
	private String profileImageUrl;

	@QueryProjection
	public AuthorDto(Long memberId, String nickname, String profileImageUrl) {
		this.memberId = memberId;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
	}
}
