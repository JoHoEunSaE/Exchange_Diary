package org.johoeunsae.exchangediary.auth.oauth2.login.google;

import feign.FeignException.FeignClientException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.GoogleOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.utils.IdentityTokenParser;
import org.johoeunsae.exchangediary.auth.oauth2.validator.Oauth2IdentityTokenValidator;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;

@RequiredArgsConstructor
@Slf4j
public class GoogleOauth2Login implements Oauth2Login {

	private static final String SUB = "sub";
	private static final String EMAIL = "email";
	private final Oauth2LoginRequestVO dto;
	private final GoogleOauth2FeignClient googleOauth2FeignClient;
	private final GoogleFeignClient googleFeignClient;
	private final GoogleOauth2Config googleOauth2Config;
	private final Oauth2IdentityTokenValidator oauth2IdentityTokenValidator;

	@Override
	public boolean isValid() {
		log.debug("Called isValid {}", dto);
		try {
			return oauth2IdentityTokenValidator.isValid(dto,
					googleOauth2FeignClient.getGooglePublicKeys(),
					googleOauth2Config.getIss(), googleOauth2Config.getClientId());
		} catch (FeignClientException e) {
			log.error("{}", e.getMessage());
			return false;
		}
	}

	@Override
	public Oauth2LoginInfoVO provideLoginInfo() {
		log.debug("Called provideLoginInfo {}", dto);
		Map<String, String> parsedClaims = IdentityTokenParser.parseClaims(dto.getValid());
		return Oauth2LoginInfoVO.builder()
				.oauthId(parsedClaims.get(SUB))
				.email(parsedClaims.get(EMAIL))
				.deviceToken(dto.getDeviceToken())
				.oauthType(dto.getOauthType())
				.refreshToken(Optional.empty())
				.build();
	}

	@Override
	public void revokeToken() {
		log.debug("Called revokeToken {}", dto);
		try {
			googleFeignClient.tokenRevoke(dto.getValid(), "");
		} catch (FeignClientException e) {
			log.error("{}", e.getMessage());
			throw new ServiceException(AuthExceptionStatus.IDENTITY_TOKEN_INVALID);
		}
	}
}
