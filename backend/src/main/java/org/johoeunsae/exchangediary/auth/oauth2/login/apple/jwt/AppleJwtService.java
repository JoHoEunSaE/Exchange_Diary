package org.johoeunsae.exchangediary.auth.oauth2.login.apple.jwt;


import static org.johoeunsae.exchangediary.utils.EnvironmentPathUtil.toStream;

import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.johoeunsae.exchangediary.auth.oauth2.config.AppleOauth2Config;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.springframework.stereotype.Service;


@Log4j2
@Service
@RequiredArgsConstructor
public class AppleJwtService {

	private final AppleOauth2Config config;
//	private final EnvironmentPathUtil pathUtil;

	public String createClientSecret() {
		LocalDateTime now = LocalDateTime.now();
		Date expirationDate = Date.from(
				now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

		Map<String, Object> jwtHeader = new HashMap<>();

		jwtHeader.put(AppleOauth2Config.KEY_ID, config.getKeyId());
		jwtHeader.put(AppleOauth2Config.ALGORITHM, config.getAlg());

		// TODO JWT 생성 security 라이브러리 사용하도록 수정
		return Jwts.builder()
				.setHeaderParams(jwtHeader)
				.setIssuer(config.getTeamId())
				.setIssuedAt(Date.from(
						now.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(expirationDate) // 만료 시간
				.setAudience(config.getAppleIdUrl())
				.setSubject(config.getBundleId())
				.signWith(getPrivateKey())
				.compact();
	}

	public PrivateKey getPrivateKey() {
		try {
			InputStream stream = toStream(config.getKeyPath());
			Reader pemReader = new InputStreamReader(stream);
			PEMParser pemParser = new PEMParser(pemReader);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
			return converter.getPrivateKey(object);
		} catch (PEMException e) {
			log.error("{}", e.getMessage());
			throw new ServiceException(AuthExceptionStatus.IDENTITY_TOKEN_INVALID);
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new ServiceException(AuthExceptionStatus.OAUTH_APPLE_KEYFILE_NOT_FOUND);
		}
	}
}
