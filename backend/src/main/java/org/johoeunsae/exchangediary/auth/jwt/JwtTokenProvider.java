package org.johoeunsae.exchangediary.auth.jwt;


import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * Jwt 토큰을 생성하는 컴포넌트
 * JwtEncoder를 사용하여 Jwt를 생성한다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtEncoder jwtEncoder;
//	private final static String TOKEN_TYPE = "typ";

	/**
	 * userId를 받아서 accessToken을 생성한다.
	 *
	 * @param id
	 * @return accessToken
	 */
	public Jwt createCommonAccessToken(Long id) {
		Instant now = Instant.now();
		JwsHeader header = JwsHeader
				.with(MacAlgorithm.HS256)
//			.header(TOKEN_TYPE, TokenType.ACCESS)
				.build();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(now)
				.expiresAt(now.plusSeconds(JwtProperties.COMMON_ACCESS_TOKEN_EXPIRE_TIME))
				.claim(JwtProperties.USER_ID, id)
				.claim(JwtProperties.ROLES, List.of(MemberRole.USER.name()))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
	}
}
