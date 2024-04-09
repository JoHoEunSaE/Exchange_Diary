package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.springframework.http.HttpStatus;

/**
 * 일기 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum NoteExceptionStatus implements ExceptionStatus {

	NOT_FOUND_NOTE(
			new ErrorReason(HttpStatus.NOT_FOUND.value(), "NO01", "존재하지 않는 일기입니다."));

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
