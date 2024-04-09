package org.johoeunsae.exchangediary.auth.oauth2.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.auth.jwt.JwtLoginTokenDto;

/**
 * 로그인 결과를 담는 VO 로그인 결과로 발급된 JWT 토큰과 새로 생성된 유저인지 여부를 포함합니다.
 */
@AllArgsConstructor
@Getter
@ToString
@Builder
public class LoginResultVO {

	private JwtLoginTokenDto jwtLoginToken;
	private boolean isNew;
}
