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
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKey;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import utils.test.UnitTest;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class PublicKeyExtractorTest extends UnitTest {

	private final static String ISSUER = "iss";
	private final static String SUBJECT = "sub";
	private final static String AUDIENCE = "aud";
	private final static String KEY_ID = "kid";
	private final static String KEY_ID_VALUE = "keyId";
	private final static String TOKEN_TYPE = "typ";
	private final static String TOKEN_TYPE_VALUE = "JWT";
	private final static String ALGORITHM = "alg";

	@Test
	@DisplayName("성공 - RSAPublicKey 객체 생성 성공")
	void 성공_generatePublicKey() {
//		given
		Map<String, String> headers = new HashMap<>();
		headers.put(ALGORITHM, "RS256");
		headers.put(KEY_ID, KEY_ID_VALUE);
		headers.put(TOKEN_TYPE, TOKEN_TYPE_VALUE);

		Oauth2PublicKey publicKey1 = new Oauth2PublicKey(
				"RSA",
				KEY_ID_VALUE,
				"sig",
				"RS256",
				"1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw",
				"AQAB"
		);

		Oauth2PublicKey publicKey2 = new Oauth2PublicKey(
				"RSA",
				"key2",
				"sig",
				"RS256",
				"1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw",
				"AQAB"
		);

		Oauth2PublicKeys publicKeys = new Oauth2PublicKeys(
				List.of(publicKey1, publicKey2)
		);

//		when, then
		Assertions.assertDoesNotThrow(
				() -> PublicKeyExtractor.generatePublicKey(headers, publicKeys));
	}

	@Test
	@DisplayName("실패 - RSAPublicKey 객체 생성 실패")
	void 실패_generatePublicKey() {
//		given
		Map<String, String> headers = new HashMap<>();
		headers.put(ALGORITHM, "RS256");
		headers.put(KEY_ID, KEY_ID_VALUE);
		headers.put(TOKEN_TYPE, TOKEN_TYPE_VALUE);

		Oauth2PublicKey publicKey1 = new Oauth2PublicKey(
				"RSA",
				"이상한키",
				"sig",
				"RS256",
				"1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw",
				"AQAB"
		);

		Oauth2PublicKeys publicKeys = new Oauth2PublicKeys(
				List.of(publicKey1)
		);

//		when, then
		Assertions.assertThrows(
				PublicKeyException.class,
				() -> PublicKeyExtractor.generatePublicKey(headers, publicKeys));
	}

	@Test
	@DisplayName("성공 - JWT 토큰 파싱 및 PublicKey 추출")
	public void 성공_parsePublicKeyAndGetClaims()
			throws JOSEException {
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

//		테스트 용 토큰
		String identityToken = jwt.getTokenValue();

//		테스트 용 공개키
		RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

//		when, then
		Assertions.assertDoesNotThrow(
				() -> PublicKeyExtractor.parsePublicKeyAndGetClaims(identityToken, publicKey));
	}

	@Test
	@DisplayName("실패 - JWT 토큰 파싱 및 PublicKey 추출")
	public void 실패_parsePublicKeyAndGetClaims()
			throws JOSEException {
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

//		테스트 용 토큰
		String identityToken = jwt.getTokenValue();

//		테스트 용 가짜 공개키
		RSAPublicKey publicKey = mock(RSAPublicKey.class);

//		when, then
		Assertions.assertThrows(
				ServiceException.class,
				() -> PublicKeyExtractor.parsePublicKeyAndGetClaims(identityToken, publicKey));
	}
}
