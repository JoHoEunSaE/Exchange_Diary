package org.johoeunsae.exchangediary.utils.update;

@FunctionalInterface
public interface UpdateApplier<T> {
	void apply(T entity);
}
