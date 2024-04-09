package org.johoeunsae.exchangediary.auth.oauth2.utils;


import static org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus.IDENTITY_TOKEN_INVALID_FORMAT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.springframework.util.Base64Utils;

@Slf4j
public abstract class IdentityTokenParser {

	private static final String IDENTITY_TOKEN_DELIMITER = "\\.";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


	public static Map<String, String> parseHeaders(String identityToken) {
		log.debug("Called parseHeaders identityToken = {}", identityToken);
		try {
			String encodedHeader = identityToken.split(
					IDENTITY_TOKEN_DELIMITER)[0];
			String decodedHeader = new String(Base64Utils.decodeFromUrlSafeString(encodedHeader));
			@SuppressWarnings("unchecked")
			Map<String, String> parsedHeaderMap = OBJECT_MAPPER.readValue(decodedHeader, Map.class);
			return parsedHeaderMap;
		} catch (Exception e) {
			throw new ServiceException(IDENTITY_TOKEN_INVALID_FORMAT);
		}
	}

	public static Map<String, String> parseClaims(String identityToken) {
		log.debug("Called parseClaims identityToken = {}", identityToken);
		try {
			String encodedClaims = identityToken.split(
					IDENTITY_TOKEN_DELIMITER)[1];
			String decodedClaims = new String(Base64Utils.decodeFromUrlSafeString(encodedClaims));
			@SuppressWarnings("unchecked")
			Map<String, String> parsedClaimsMap = OBJECT_MAPPER.readValue(decodedClaims, Map.class);
			return parsedClaimsMap;
		} catch (Exception e) {
			throw new ServiceException(IDENTITY_TOKEN_INVALID_FORMAT);
		}
	}
}
