package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

/**
 * 멤버 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum MemberExceptionStatus implements ExceptionStatus {

	NOT_FOUND_MEMBER(
			new ErrorReason(HttpStatus.NOT_FOUND.value(), "ME01", "존재하지 않는 멤버입니다.")
	),
	NEED_AUTH_CODE(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME02", "AUTH 인증 코드가 필요합니다.")
	),
	INVALID_NICKNAME(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME03", "올바른 닉네임이 아닙니다.")
	),
	INVALID_STATEMENT(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME04", "올바른 한 줄 소개가 아닙니다.")
	),
	NOT_POSSIBLE_PERIOD(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME05", "닉네임 변경 가능 기간이 아닙니다.")
	),
	DUPLICATE_NICKNAME(
			new ErrorReason(HttpStatus.CONFLICT.value(), "ME06", "고유하지 않은 사용자 이름입니다")
	),
	TOO_LONG_NICKNAME(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME07", "닉네임은 10자 이하로 작성해 주세요.")
	),
	NOT_ALLOWED_NICKNAME(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME08", "닉네임에는 한글, 영어, 숫자, '-', 및 '_'만 사용할 수 있습니다.")
	),
	TOO_LONG_STATEMENT(
			new ErrorReason(HttpStatus.BAD_REQUEST.value(), "ME09", "한 줄 소개는 31자 이하로 작성해 주세요.")
	)
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
