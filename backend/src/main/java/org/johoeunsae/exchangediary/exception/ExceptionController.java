package org.johoeunsae.exchangediary.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.Messages;
import org.johoeunsae.exchangediary.exception.status.ExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.ValidationExceptionStatusResolver;
import org.johoeunsae.exchangediary.exception.utils.DiscordWebhookErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

	private final ValidationExceptionStatusResolver validationExceptionStatusResolver;
	private final DiscordWebhookErrorHandler discordWebhookErrorHandler;

	@ExceptionHandler(ControllerException.class)
	public ResponseEntity<?> controllerExceptionHandler(ControllerException e) {
		// TODO: log로 관리
		e.printStackTrace();
		return ResponseEntity
				.status(e.getErrorReason().getStatusCode())
				.body(e.getErrorReason());
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<?> serviceExceptionHandler(ServiceException e) {
		e.printStackTrace();
		return ResponseEntity
				.status(e.getErrorReason().getStatusCode())
				.body(e.getErrorReason());
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<?> domainExceptionHandler(DomainException e) {
		e.printStackTrace();
		return ResponseEntity
				.status(e.getErrorReason().getStatusCode())
				.body(e.getErrorReason());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
		e.printStackTrace();
		Map<String, Object> errors = new HashMap<>();
		errors.put("success", false);
		errors.put("errorReason", e.getMessage());
		errors.put("timestamp", LocalDateTime.now());
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.body(errors);
	}

	@ExceptionHandler({
			BindException.class,
	})
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(
			BindException e) {
		e.printStackTrace();
		Map<String, Object> errors = new LinkedHashMap<>();

		BindingResult result = e.getBindingResult();

		String code = result.getAllErrors().get(0).getDefaultMessage();

		ExceptionStatus status = validationExceptionStatusResolver.findByErrorCode(
				Objects.requireNonNull(code));

		errors.put("success", false);
		errors.put("errorReason", status.getErrorReason());
		errors.put("timestamp", LocalDateTime.now());

		return ResponseEntity
				.status(status.getErrorReason().getStatusCode())
				.body(errors);
	}

	@ExceptionHandler({
			MaxUploadSizeExceededException.class,
	})
	public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededException(
			MaxUploadSizeExceededException e) {
		e.printStackTrace();
		Map<String, String> errors = new HashMap<>();

		errors.put("coverImageData", Messages.EXCEED_COVER_IMAGE_SIZE_VALUE);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(errors);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleInternalServerErrorException(Exception e, HttpServletRequest request) {
		log.error(e.getMessage(), e);
		// 현재 요청의 URL 가져오기
		String requestUrl = request.getRequestURL().toString();
		String requestMethod = request.getMethod();
		// 메시지를 디스코드 웹훅으로 전송
		discordWebhookErrorHandler.sendErrorToDiscordWebhook(e, requestUrl, requestMethod);
	}
}
