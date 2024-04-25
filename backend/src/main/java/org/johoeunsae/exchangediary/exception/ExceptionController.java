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
import org.johoeunsae.exchangediary.exception.utils.DiscordWebErrorMessage;
import org.johoeunsae.exchangediary.exception.utils.DiscordWebhookErrorSender;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason.ErrorReasonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController extends ResponseEntityExceptionHandler {

	private final DiscordWebhookErrorSender discordWebhookErrorSender;
	private final ValidationExceptionStatusResolver validationExceptionStatusResolver;
	private static final String DEFAULT_SPRING_MVC_ERROR_MESSAGE_VALUE = "Spring MVC 에서 예기치 않은 오류가 발생했어요.🥲";
	private static final String DEFAULT_ERROR_MESSAGE_VALUE = "다이어리 서버에서 예기치 않은 오류가 발생했어요.🥲";

//	=========== Start of Exchange Diary Exception Handler ===========

	@ExceptionHandler(ControllerException.class)
	public ResponseEntity<?> controllerExceptionHandler(ControllerException e) {
		log.info("[ControllerException] {}", e.getErrorReason());
		if (log.isDebugEnabled()) {
			log.debug("Exception stack trace: ", e);
		}
		return ResponseEntity
				.status(e.getErrorReason().getStatusCode())
				.body(e.getErrorReason());
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<?> serviceExceptionHandler(ServiceException e) {
		log.info("[ServiceException] {}", e.getErrorReason());
		if (log.isDebugEnabled()) {
			log.debug("Exception stack trace: ", e);
		}
		return ResponseEntity
				.status(e.getErrorReason().getStatusCode())
				.body(e.getErrorReason());
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<?> domainExceptionHandler(DomainException e) {
		log.warn("[DomainException] {}", e.getErrorReason());
		if (log.isDebugEnabled()) {
			log.debug("Exception stack trace: ", e);
		}
		return ResponseEntity
				.status(e.getErrorReason().getStatusCode())
				.body(e.getErrorReason());
	}

//	=========== End of Exchange Diary Exception Handler ===========

//	=========== Start of Spring Security Exception Handler ===========

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("[AccessDeniedException] {}", e.getMessage());
		ErrorReason errorReason = ErrorReason.builder()
				.statusCode(HttpStatus.UNAUTHORIZED.value())
				.code("SECURITY")
				.message(e.getMessage())
				.build();
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(errorReason);
	}

//	=========== End of Spring Security Exception Handler ===========

//	=========== Start of Spring MVC Exception Handler ===========

	@ExceptionHandler({
			MaxUploadSizeExceededException.class,
	})
	public ResponseEntity<Object> handleMaxUploadSizeExceededException(
			MaxUploadSizeExceededException e) {
//		FIXME: 응답 형식의 통일이 필요합니다.
//		log.warn("[MaxUploadSizeExceededException] {}", e.getMessage());
//		ErrorReason errorReason = ErrorReason.builder()
//				.statusCode(HttpStatus.BAD_REQUEST.value())
//				.code("SPRINGWEB")
//				.message(e.getMessage())
//				.build();
//		return ResponseEntity
//				.status(HttpStatus.BAD_REQUEST)
//				.body(errorReason);
		log.warn("[MaxUploadSizeExceededException] {}", e.getMessage());
		Map<String, String> errors = new HashMap<>();

		errors.put("coverImageData", Messages.EXCEED_COVER_IMAGE_SIZE_VALUE);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(errors);
	}

	private ResponseEntity<Object> handleValidationExceptions(
			BindException e) {
//		FIXME: 응답 형식의 통일이 필요합니다.
//		BindingResult result = e.getBindingResult();
//		String code = result.getAllErrors().get(0).getDefaultMessage();
//		ExceptionStatus status = validationExceptionStatusResolver.findByErrorCode(
//				Objects.requireNonNull(code));
//
//		return ResponseEntity
//				.status(status.getErrorReason().getStatusCode())
//				.body(status.getErrorReason());
		Map<String, Object> errors = new LinkedHashMap<>();

		BindingResult result = e.getBindingResult();

		String code = result.getAllErrors().get(0).getDefaultMessage();

		ExceptionStatus status = validationExceptionStatusResolver.findByErrorCode(
				Objects.requireNonNull(code));
		log.warn("[BindException] {}", status.getErrorReason().getMessage());

		errors.put("success", false);
		errors.put("errorReason", status.getErrorReason());
		errors.put("timestamp", LocalDateTime.now());

		return ResponseEntity
				.status(status.getErrorReason().getStatusCode())
				.body(errors);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception e, Object body,
			org.springframework.http.HttpHeaders headers,
			org.springframework.http.HttpStatus status,
			org.springframework.web.context.request.WebRequest request) {
		if (e instanceof BindException) {
			return handleValidationExceptions((BindException) e);
		}
		String requestUri = request.getContextPath();
		ErrorReasonBuilder errorReasonBuilder = ErrorReason.builder()
				.statusCode(status.value())
				.code("SPRINGMVC")
				.message(e.getMessage());
		ErrorReason errorReason;

		if (status.is5xxServerError()) {
			errorReasonBuilder.message(DEFAULT_SPRING_MVC_ERROR_MESSAGE_VALUE);
			log.error("[SpringMVCServerError] {}: {} at {}",
					status.getReasonPhrase(),
					e.getMessage(),
					requestUri);
			errorReason = errorReasonBuilder.build();
			log.error("Exception stack trace: ", e);
			discordWebhookErrorSender.sendWebErrorMessage(
					DiscordWebErrorMessage.fromWebRequest(
							request,
							DEFAULT_SPRING_MVC_ERROR_MESSAGE_VALUE,
							errorReason.toString()
					)
			);
		} else {
			log.warn("[SpringMVCClientError] {}: {} at {}",
					status.getReasonPhrase(),
					e.getMessage(),
					requestUri);
			errorReason = errorReasonBuilder.build();
		}
		return ResponseEntity
				.status(status)
				.headers(headers)
				.body(errorReason);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleInternalServerErrorException(Exception e,
			HttpServletRequest request) {
		log.error("[UncheckedException] {} for request URL: {}", e.getMessage(),
				request.getRequestURL());
		log.error("Exception stack trace: ", e);

		ErrorReason errorReason = ErrorReason.builder()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.code("UNKNOWN")
				.message(DEFAULT_ERROR_MESSAGE_VALUE)
				.build();

		discordWebhookErrorSender.sendWebErrorMessage(
				DiscordWebErrorMessage.fromHttpServletRequest(
						request,
						DEFAULT_ERROR_MESSAGE_VALUE,
						errorReason.toString()
				)
		);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorReason);
	}
}
