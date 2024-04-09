package org.johoeunsae.exchangediary.exception.utils;

import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus;
import org.johoeunsae.exchangediary.utils.domain.IdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

import java.io.Serializable;

@Log4j2
public abstract class RuntimeExceptionThrower {

	public static void ifTrue(boolean condition, RuntimeException exception) {
		if (condition) {
			throw exception;
		}
	}

	public static void ifFalse(boolean condition, RuntimeException exception) {
		if (!condition) {
			throw exception;
		}
	}

	public static void validateDomain(Validatable validatable) {
		if (!validatable.isValid()) {
			throw new DomainException(CommonExceptionStatus.INCORRECT_ARGUMENT);
		}
	}

	public static <ID extends Serializable> void checkIdLoaded(IdDomain<ID> domain) {
		if (domain == null || domain.getId() == null) {
			log.warn("로딩되지 않은 {} 객체를 사용하려고 합니다.",
					domain == null ? "null" : domain.getClass().getTypeName());
			throw new DomainException(CommonExceptionStatus.INCORRECT_ARGUMENT);
		}
	}
}
