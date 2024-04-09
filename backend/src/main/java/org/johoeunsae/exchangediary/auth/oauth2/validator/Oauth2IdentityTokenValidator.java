package org.johoeunsae.exchangediary.auth.oauth2.validator;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.utils.IdentityTokenParser;
import org.johoeunsae.exchangediary.auth.oauth2.utils.PublicKeyException;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2PublicKeys;
import org.johoeunsae.exchangediary.auth.oauth2.utils.PublicKeyExtractor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2IdentityTokenValidator {

	public boolean isValid(Oauth2LoginRequestVO dto, Oauth2PublicKeys publicKeys, String issuer,
			String clientId) {
		log.debug("Called isValid dto = {}, publicKeys= {}, issuer = {}, clientId = {}", dto,
				publicKeys,
				issuer, clientId);
		String idToken = dto.getValid();
		Map<String, String> headers = IdentityTokenParser.parseHeaders(idToken);
		Jwt jwt;
		try {
			RSAPublicKey publicKey = PublicKeyExtractor.generatePublicKey(headers, publicKeys);
			jwt = PublicKeyExtractor.parsePublicKeyAndGetClaims(idToken,
					publicKey);
		} catch (PublicKeyException e) {
			log.info("INVALID_PUBLIC_KEY: {}", e.getMessage());
			return false;
		}

		if (jwt.getIssuer() == null || !jwt.getIssuer().toExternalForm().equals(issuer)) {
			log.info("UNKNOWN_OAUTH_ISSUER: {}", jwt.getIssuer());
			return false;
		}

		if (jwt.getAudience() == null || !jwt.getAudience().contains(clientId)) {
			log.info("UNKNOWN_OAUTH_AUDIENCE: {}", jwt.getAudience());
			return false;
		}

		return true;
	}
}
