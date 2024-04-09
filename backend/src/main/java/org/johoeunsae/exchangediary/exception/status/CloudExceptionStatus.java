package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

/**
 * 클라우드 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum CloudExceptionStatus implements ExceptionStatus {
	S3_UPLOAD_ERROR(new ErrorReason(HttpStatus.BAD_GATEWAY.value(), "CLOUD01",
			"이미지 업로드 중 문제가 발생했습니다.")),
	S3_DELETE_ERROR(new ErrorReason(HttpStatus.BAD_GATEWAY.value(), "CLOUD02",
			"이미지 삭제 중 문제가 발생했습니다.")),
	IMAGE_NOT_FOUND(new ErrorReason(HttpStatus.NOT_FOUND.value(), "CLOUD03",
			"이미지를 찾을 수 없습니다."));

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
