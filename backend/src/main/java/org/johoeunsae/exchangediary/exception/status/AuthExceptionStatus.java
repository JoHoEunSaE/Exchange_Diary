package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

/**
 * 인증 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum AuthExceptionStatus implements ExceptionStatus {

	NOT_FOUND_SOCIAL_INFO(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "AU11", "소셜 정보가 입력되지 않았습니다.")
	),
	NOT_ACTIVE_MEMBER(
			new ErrorReason(HttpStatus.FORBIDDEN.value(), "AU10", "삭제된 멤버입니다.")
	),
	UNKNOWN_OAUTH_AUDIENCE(
			new ErrorReason(HttpStatus.UNAUTHORIZED.value(), "AU09", "알 수 없는 OAuth 클라이언트입니다.")),
	UNKNOWN_OAUTH_ISSUER(
			new ErrorReason(HttpStatus.UNAUTHORIZED.value(), "AU08", "알 수 없는 OAuth 발급자입니다.")),
	IDENTITY_TOKEN_INVALID(
			new ErrorReason(HttpStatus.UNAUTHORIZED.value(), "AU007", "토큰이 유효하지 않습니다.")),
	IDENTITY_TOKEN_EXPIRED(
			new ErrorReason(HttpStatus.UNAUTHORIZED.value(), "AU006", "ID 토큰이 만료되었습니다.")),
	IDENTITY_TOKEN_INVALID_FORMAT(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "AU05", "ID 토큰 형식이 올바르지 않습니다.")),
	UNAUTHENTICATED_MEMBER(new ErrorReason(HttpStatus.FORBIDDEN.value(), "AU004", "권한이 없는 요청입니다.")),
	UNAUTHORIZED_MEMBER(
			new ErrorReason(HttpStatus.UNAUTHORIZED.value(), "AU003", "인증되지 않은 멤버입니다.")),
	ALREADY_EXIST_MEMBER(new ErrorReason(HttpStatus.CONFLICT.value(), "AU002", "이미 가입된 멤버입니다.")),
	OAUTH_BAD_GATEWAY(
			new ErrorReason(HttpStatus.BAD_GATEWAY.value(), "AU001", "Oauth2 로그인에 실패하였습니다.")),
	NOT_FOUND_MEMBER(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "AU011", "멤버를 찾을 수 없습니다.")),

	NOT_FOUND_APPLE_TOKEN(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "AU012", "APPLE 토큰이 존재하지 않습니다.")),
	OAUTH_APPLE_KEYFILE_NOT_FOUND(
			new ErrorReason(HttpStatus.NOT_FOUND.value(), "AU999", "APPLE 인증키가 존재하지 않습니다.")),
	OAUTH_APPLE_KEYFILE_INVALID(
			new ErrorReason(HttpStatus.NOT_FOUND.value(), "AU998", "APPLE 인증키가 유효하지 않습니다.")),

	;

	private final ErrorReason errorReason;

	@Override
	public ControllerException toControllerException() {
		return new ControllerException(this);
	}

	@Override
	public ServiceException toServiceException() {
		return new ServiceException(this);
	}

	@Override
	public DomainException toDomainException() {
		return new DomainException(this);
	}

	@Override
	public ErrorReason getErrorReason() {
		return this.errorReason;
	}
}
