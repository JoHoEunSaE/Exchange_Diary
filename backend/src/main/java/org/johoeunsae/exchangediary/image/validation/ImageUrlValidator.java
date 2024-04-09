package org.johoeunsae.exchangediary.image.validation;

import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageUrlValidator implements
		ConstraintValidator<ImageUrlValidation, String> {
	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
			".jpg", ".jpeg", ".png"
	);

	private boolean validateImageUrl(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			context.buildConstraintViolationWithTemplate(
							"UTILS03")
					.addConstraintViolation();
			return false;
		}
		if (!value.contains(".")) {
			context.buildConstraintViolationWithTemplate(
							"UTILS02")
					.addConstraintViolation();
			return false;
		}
		String fileExtension = value.substring(value.lastIndexOf("."));
		if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			context.buildConstraintViolationWithTemplate(
							"UTILS02")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		return validateImageUrl(value, context);
	}
}
