package org.johoeunsae.exchangediary.diary.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.johoeunsae.exchangediary.dto.NoteUpdateDto;

public class NoteUpdateValidator implements
		ConstraintValidator<NoteUpdateValidation, NoteUpdateDto> {

	private static final int TITLE_MAX_LENGTH = 63;
	private static final int CONTENT_MAX_LENGTH = 4095;

	private boolean validateTitle(NoteUpdateDto value, ConstraintValidatorContext context) {
		if (value.getTitle() != null && !value.getTitle().isEmpty()) {
			if (value.getTitle().length() > TITLE_MAX_LENGTH) {
				context.buildConstraintViolationWithTemplate(
								"DIARY017")
						.addConstraintViolation();
				return false;
			}
			return true;
		}
		context.buildConstraintViolationWithTemplate(
						"DIARY007")
				.addConstraintViolation();
		return false;
	}

	private boolean validateContent(NoteUpdateDto value,
			ConstraintValidatorContext context) {
		if (value.getContent() != null && !value.getContent().isEmpty()) {
			if (value.getContent().length() > CONTENT_MAX_LENGTH) {
				context.buildConstraintViolationWithTemplate(
								"DIARY019")
						.addConstraintViolation();
				return false;
			}
			return true;
		}
		context.buildConstraintViolationWithTemplate(
						"DIARY018")
				.addConstraintViolation();
		return false;
	}

	@Override
	public boolean isValid(NoteUpdateDto value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		return validateTitle(value, context) && validateContent(value, context);
	}
}
