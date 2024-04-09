package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

/**
 * 공통적으로 사용되는 예외 상태 코드를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum CommonExceptionStatus implements ExceptionStatus {
	UNAUTHENTICATED(
			new ErrorReason(HttpStatus.FORBIDDEN.value(), "CM04", "권한이 없는 접근입니다.")),
	NOT_PERSISTED(
			new ErrorReason(HttpStatus.INTERNAL_SERVER_ERROR.value(), "CM03", "영속되지 않은 객체입니다.")),
	INTERNAL_SERVER_ERROR(
			new ErrorReason(HttpStatus.INTERNAL_SERVER_ERROR.value(), "CM02", "서버 에러가 발생했습니다")),
	INCORRECT_ARGUMENT(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "CM01", "잘못된 입력입니다.")),
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
