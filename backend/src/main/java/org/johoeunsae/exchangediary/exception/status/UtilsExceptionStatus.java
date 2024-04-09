package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

/**
 * 유틸리티 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum UtilsExceptionStatus implements ExceptionStatus {
	INVALID_FILE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "UTILS01",
			"올바르지 않은 파일입니다.")),
	INVALID_FILE_EXTENSION(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "UTILS02",
			"올바르지 않은 확장자입니다.")),
	INVALID_FILE_URL(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "UTILS03",
			"올바르지 않은 파일 경로입니다.")),
	INVALID_IMAGE_TYPE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "UTILS04",
			"올바르지 않은 이미지 타입입니다.")),
	NON_MAPPED_TARGET(new ErrorReason(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UTILS05",
			"매핑되지 않은 타겟입니다.")),
	NON_MAPPED_FIELD(new ErrorReason(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UTILS06",
			"매핑되지 않은 필드입니다.")),
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
