package org.johoeunsae.exchangediary.utils.update;

public class UpdateException extends RuntimeException {
	Status status;
	public enum Status {
		IllegalState,
		IllegalArgument,
	}
	public UpdateException(Status status) {
		this.status = status;
	}

	public UpdateException(String message, Status status) {
		super(message);
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
}
