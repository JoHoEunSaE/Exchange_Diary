package org.johoeunsae.exchangediary.exception.status;

import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.johoeunsae.exchangediary.exception.ServiceException;

/**
 * ExceptionStatus를 구현한 Enum 클래스들은 각각의 Exception을 생성할 수 있어야 한다.
 * ExceptionStatus는 도메인 별로 구현되어야 한다. (Auth, Diary, Note, User 등)
 * // @formatter:off
 * <p>s
 *     toControllerException() : ControllerException을 생성한다.
 *     toServiceException() : ServiceException을 생성한다.
 *     toDomainException() : DomainException을 생성한다.
 *     getErrorReason() : ErrorReason을 반환한다.
 * </p>
 * // @formatter:on
 *
 */
public interface ExceptionStatus {

	ControllerException toControllerException();

	ServiceException toServiceException();

	DomainException toDomainException();

	ErrorReason getErrorReason();
}
