package org.johoeunsae.exchangediary.utils.update;

@FunctionalInterface
public interface UpdateValidator<T> {
	void validate(T entity) throws UpdateException;
}
