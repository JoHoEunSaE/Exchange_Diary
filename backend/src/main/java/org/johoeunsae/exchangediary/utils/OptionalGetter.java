package org.johoeunsae.exchangediary.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class OptionalGetter {
	public static <T, U> Optional<U> get(T value, Function<T, U> getter) {
		if (Objects.isNull(value))
			return Optional.empty();
		return Optional.ofNullable(getter.apply(value));
	}
}
