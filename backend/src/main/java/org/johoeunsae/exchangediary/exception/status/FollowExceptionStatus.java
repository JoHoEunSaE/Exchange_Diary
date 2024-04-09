package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;


/**
 * 팔로우 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum FollowExceptionStatus implements ExceptionStatus {
	SELF_FOLLOW(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "FO01", "자기 자신은 팔로우 할 수 없습니다.")),
	DOUBLE_FOLLOW(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "FO02", "이미 팔로우 된 사람은 다시 팔로우할 수 없습니다."))
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
