package org.johoeunsae.exchangediary.auth.oauth2.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.member.domain.OauthType;

@Getter
@Builder
@ToString
public class Oauth2LoginRequestVO {

	@Schema(description = "ID 토큰 or Access 토큰 or Authorization 코드", example = "JWT.토큰.이지롱")
	private final String valid;

	@Schema(description = "Oauth2 타입", example = "KAKAO", allowableValues = "KAKAO, NAVER, GOOGLE, APPLE", defaultValue = "KAKAO")
	private final OauthType oauthType;

	@Schema(description = "디바이스 토큰", example = "디바이스 토큰")
	private final String deviceToken;

}
