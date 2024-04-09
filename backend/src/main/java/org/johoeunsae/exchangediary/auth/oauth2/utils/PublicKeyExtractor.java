package org.johoeunsae.exchangediary.auth.oauth2.utils;


import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.IDENTITY_TOKEN_INVALID;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKey;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
@Slf4j
public abstract class PublicKeyExtractor {

	private static final String ALGORITHM = "alg";
	private static final String KEY_ID = "kid";
	private static final int POSITIVE_SIGN_NUMBER = 1;

	public static RSAPublicKey generatePublicKey(Map<String, String> headers,
			Oauth2PublicKeys publicKeys) throws PublicKeyException {
		log.debug("Called generatePublicKey headers = {}, publicKeys = {}", headers, publicKeys);
		Oauth2PublicKey oauth2PublicKey =
				publicKeys.getMatchesKey(headers.get(ALGORITHM),
								headers.get(KEY_ID))
						.orElseThrow(() -> new PublicKeyException("alg or kid가 일치하지 않습니다."));

		byte[] nBytes = Base64Utils.decodeFromUrlSafeString(oauth2PublicKey.getN());
		byte[] eBytes = Base64Utils.decodeFromUrlSafeString(oauth2PublicKey.getE());

		BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
		BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

		try {
			KeyFactory keyFactory = KeyFactory.getInstance(oauth2PublicKey.getKty());
			return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
			throw new PublicKeyException("Public Key 생성에 문제가 발생했습니다.");
		}
	}

	public static Jwt parsePublicKeyAndGetClaims(String idToken,
			RSAPublicKey publicKey) {
		log.debug("Called parsePublicKeyAndGetClaims idToken = {}, publicKey = {}", idToken,
				publicKey);
		try {
			NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
			return jwtDecoder.decode(idToken);
		} catch (JwtException e) {
			log.info("Jwt 파싱 과정에서 에러가 발생했습니다: {}", e.getMessage());
			throw new ServiceException(IDENTITY_TOKEN_INVALID);
		}
	}
}
