package org.johoeunsae.exchangediary.exception.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class ErrorReason {

	private final int statusCode;
	private final String code;
	private final String message;
}
