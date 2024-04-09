package org.johoeunsae.exchangediary.exception;

import org.johoeunsae.exchangediary.exception.utils.ErrorReason;

/**
 * DomainException, ServiceException, ControllerException 의 공통 인터페이스입니다.
 */
public interface BaseException {

	ErrorReason getErrorReason();
}
