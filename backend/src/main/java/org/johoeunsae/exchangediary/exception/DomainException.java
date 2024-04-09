package org.johoeunsae.exchangediary.exception;

import org.johoeunsae.exchangediary.exception.status.ExceptionStatus;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

public class DomainException extends RuntimeException implements BaseException {

	private final ErrorReason errorReason;

	/**
	 * @param status exception에 대한 정보에 대한 enum
	 */
	public DomainException(ExceptionStatus status) {
		this.errorReason = status.getErrorReason();
	}

	public DomainException(HttpStatus status, String message) {
		this.errorReason = new ErrorReason(status.value(), message, status.getReasonPhrase());
	}

	@Override
	public ErrorReason getErrorReason() {
		return this.errorReason;
	}
}
