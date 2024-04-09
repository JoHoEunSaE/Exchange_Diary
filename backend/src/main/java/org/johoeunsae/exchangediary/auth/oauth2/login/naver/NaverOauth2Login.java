package org.johoeunsae.exchangediary.auth.oauth2.login.naver;

import feign.FeignException.FeignClientException;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.johoeunsae.exchangediary.auth.oauth2.config.NaverOauth2Config;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.vo.AccessTokenValidationVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.NaverLoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.johoeunsae.exchangediary.exception.ServiceException;

@RequiredArgsConstructor
@Slf4j
public class NaverOauth2Login implements Oauth2Login {

	private final Oauth2LoginRequestVO dto;
	private final NaverFeignClient naverFeignClient;
	private final NaverOauth2Config naverOauth2Config;
	private final NaverNidFeignClient naverNidFeignClient;

	@Override
	public boolean isValid() {
		log.debug("Called isValid {}", dto);
		try {
			AccessTokenValidationVO result = naverFeignClient.verifyAccessToken(
					"Bearer " + dto.getValid());
//			TODO: 하드코딩된 값 수정
//			TODO: 추후 다른 소셜 로그인 추가 시, 공통으로 사용될 수 있도록 계층 추가
			if ("00".equals(result.getResultCode())) {
				return true;
			}
		} catch (FeignClientException e) {
			log.error("{}", e.getMessage());
			if (e.status() == 401) {
				throw new ServiceException(AuthExceptionStatus.UNAUTHORIZED_MEMBER);
			} else if (e.status() == 502) {
				throw new ServiceException(AuthExceptionStatus.OAUTH_BAD_GATEWAY);
			}
		}
		return false;
	}

	@Override
	public Oauth2LoginInfoVO provideLoginInfo() {
		log.debug("Called provideLoginInfo {}", dto);
		try {
			NaverLoginInfoVO result = naverFeignClient.getLoginUserInfo("Bearer " + dto.getValid());
//			TODO: 하드코딩된 값 수정
//			TODO: 추후 다른 소셜 로그인 추가 시, 공통으로 사용될 수 있도록 계층 추가
			Oauth2LoginInfoVO build = Oauth2LoginInfoVO.builder()
					.oauthId(result.getResponse().getOauthId())
					.email(result.getResponse().getEmail())
					.deviceToken(dto.getDeviceToken())
					.oauthType(dto.getOauthType())
					.refreshToken(Optional.empty())
					.build();
			return build;
		} catch (FeignClientException e) {
			log.error("{}", e.getMessage());
			if (e.status() == HttpServletResponse.SC_UNAUTHORIZED) {
				throw new ServiceException(AuthExceptionStatus.UNAUTHORIZED_MEMBER);
			} else if (e.status() == HttpServletResponse.SC_BAD_GATEWAY) {
				throw new ServiceException(AuthExceptionStatus.OAUTH_BAD_GATEWAY);
			}
			return null;
		}
	}

	@Override
	public void revokeToken() {
		log.debug("Called revokeToken {}", dto);
		try {
			naverNidFeignClient.revokeToken("delete", naverOauth2Config.getClientId(),
					naverOauth2Config.getClientSecret(), dto.getValid(), "NAVER");
		} catch (FeignClientException e) {
			log.error("{}", e.getMessage());
			throw new ServiceException(AuthExceptionStatus.IDENTITY_TOKEN_INVALID);
		}
	}
}
