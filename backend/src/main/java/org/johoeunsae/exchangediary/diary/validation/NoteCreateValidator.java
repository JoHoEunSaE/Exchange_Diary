package org.johoeunsae.exchangediary.diary.validation;

import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.johoeunsae.exchangediary.dto.NoteCreateRequestDto;

public class NoteCreateValidator implements
		ConstraintValidator<NoteCreateValidation, NoteCreateRequestDto> {

	private static final int TITLE_MAX_LENGTH = 63;
	private static final int CONTENT_MAX_LENGTH = 4095;
	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
			".jpg", ".jpeg", ".png"
	);

	private boolean validateTitle(NoteCreateRequestDto value, ConstraintValidatorContext context) {
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

	private boolean validateContent(NoteCreateRequestDto value,
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

	private boolean validateImages(NoteCreateRequestDto value,
			ConstraintValidatorContext context) {
		if (value.getImageUrls() != null && !value.getImageUrls().isEmpty()) {
			for (String imageUrl : value.getImageUrls()) {
				//		확장자 체크
				if (!imageUrl.contains(".")) {
					context.buildConstraintViolationWithTemplate(
									"DIARY021")
							.addConstraintViolation();
					return false;
				}
				String fileExtension = imageUrl.substring(imageUrl.lastIndexOf("."));
				if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
					context.buildConstraintViolationWithTemplate(
									"DIARY021")
							.addConstraintViolation();
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isValid(NoteCreateRequestDto value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		return validateTitle(value, context) && validateContent(value, context) && validateImages(
				value, context);
	}
}
