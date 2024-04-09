package org.johoeunsae.exchangediary.exception.utils;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ErrorResponse {

	private final boolean success = false;
	private final ErrorReason errorReason;
	private final LocalDateTime timeStamp;


	public ErrorResponse(ErrorReason errorReason) {
		this.errorReason = errorReason;
		this.timeStamp = LocalDateTime.now();
	}

	public ErrorResponse(int status, String code, String reason, String path) {
		this.errorReason = new ErrorReason(status, code, reason);
		this.timeStamp = LocalDateTime.now();
	}
}
