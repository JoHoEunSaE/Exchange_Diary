package org.johoeunsae.exchangediary.auth.oauth2.utils;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import utils.test.UnitTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class IdentityTokenParserTest extends UnitTest {

	private final static String ISSUER = "iss";
	private final static String SUBJECT = "sub";
	private final static String AUDIENCE = "aud";
	private final static String KEY_ID = "kid";
	private final static String KEY_ID_VALUE = "keyId";
	private final static String TOKEN_TYPE = "typ";
	private final static String TOKEN_TYPE_VALUE = "JWT";
	private final static String ALGORITHM = "alg";

	@Test
	@DisplayName("성공 - 토큰 헤더 파싱 테스트")
	public void 성공_parseTokenHeaderTest() throws JOSEException {
//      given
		JwsHeader header = JwsHeader
				.with(SignatureAlgorithm.RS256)
				.header(KEY_ID, KEY_ID_VALUE)
				.header(TOKEN_TYPE, TOKEN_TYPE_VALUE)
				.build();

		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(SUBJECT)
				.audience(List.of(AUDIENCE))
				.issuedAt(now)
				.expiresAt(now.plusSeconds(3600))
				.build();

		RSAKey rsaKey = new RSAKeyGenerator(2048)
				.keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.RS256)
				.keyID(KEY_ID_VALUE)
				.generate();

		JWKSet jwkSet = new JWKSet(rsaKey);

		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

		JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);

		Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
		String identityToken = jwt.getTokenValue();

//	    when
		Map<String, String> parsedHeaderMap = IdentityTokenParser.parseHeaders(identityToken);

//		then
		Assertions.assertEquals(parsedHeaderMap.get(KEY_ID), KEY_ID_VALUE);
		Assertions.assertEquals(parsedHeaderMap.get(TOKEN_TYPE), TOKEN_TYPE_VALUE);
		Assertions.assertEquals(parsedHeaderMap.get(ALGORITHM), SignatureAlgorithm.RS256.getName());
	}

	@Test
	@DisplayName("실패 - 토큰 헤더 파싱 테스트")
	public void 실패_parseTokenHeaderTest() {
//      given
		String identityToken = "은비킴CPP힘내요";

//		when
		Assertions.assertThrows(ServiceException.class, () -> {
			IdentityTokenParser.parseHeaders(identityToken);
		});
	}

	@Test
	@DisplayName("성공 - 토큰 payload 파싱 테스트")
	public void 성공_parseClaimsHeaderTest() throws JOSEException {
//      given
		JwsHeader header = JwsHeader
				.with(SignatureAlgorithm.RS256)
				.header(KEY_ID, KEY_ID_VALUE)
				.header(TOKEN_TYPE, TOKEN_TYPE_VALUE)
				.build();

		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(SUBJECT)
				.claim("email", "은피피@gmail.com")
				.audience(List.of(AUDIENCE))
				.issuedAt(now)
				.expiresAt(now.plusSeconds(3600))
				.build();

		RSAKey rsaKey = new RSAKeyGenerator(2048)
				.keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.RS256)
				.keyID(KEY_ID_VALUE)
				.generate();

		JWKSet jwkSet = new JWKSet(rsaKey);

		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

		JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);

		Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
		String identityToken = jwt.getTokenValue();

//	    when
		Map<String, String> parsedClaimsMap = IdentityTokenParser.parseClaims(identityToken);

//		then
		Assertions.assertEquals(parsedClaimsMap.get(ISSUER), ISSUER);
		Assertions.assertEquals(parsedClaimsMap.get(SUBJECT), SUBJECT);
		Assertions.assertEquals(parsedClaimsMap.get("email"), "은피피@gmail.com");
		Assertions.assertEquals(parsedClaimsMap.get(AUDIENCE), AUDIENCE);
	}

	@Test
	@DisplayName("실패 - 토큰 payload 파싱 테스트")
	public void 실패_parseClaimsHeaderTest() {
//      given
		String identityToken = "은비킴CPP힘내요";

//		when
		Assertions.assertThrows(ServiceException.class, () -> {
			IdentityTokenParser.parseHeaders(identityToken);
		});
	}
}
