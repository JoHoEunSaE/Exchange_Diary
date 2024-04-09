package org.johoeunsae.exchangediary.diary.validation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.johoeunsae.exchangediary.dto.DiaryUpdateRequestDto;

public class DiaryUpdateValidator implements
		ConstraintValidator<DiaryUpdateValidation, DiaryUpdateRequestDto> {

	private static final int TITLE_MAX_LENGTH = 31;
	private static final int GROUP_NAME_MAX_LENGTH = 15;
	private static final String COLOR_COVER_REGEX = "^#[0-9a-fA-F]{8}";
	private static final Pattern COLOR_COVER_REGEX_PATTERN = Pattern.compile(COLOR_COVER_REGEX);
	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
			".jpg", ".jpeg", ".png"
	);

	private boolean validateColorCover(DiaryUpdateRequestDto value,
			ConstraintValidatorContext context) {
//		값 유무 체크
		if (value.getCoverData() == null || value.getCoverData().isEmpty()) {
			context.buildConstraintViolationWithTemplate(
							"DIARY001")
					.addConstraintViolation();
			return false;
		}
//		문법 오류 체크
		if (!COLOR_COVER_REGEX_PATTERN.matcher(value.getCoverData()).matches()) {
			context.buildConstraintViolationWithTemplate(
							"DIARY002")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	private boolean validateImageCover(DiaryUpdateRequestDto value,
			ConstraintValidatorContext context) {
//		값 유무 체크
		if (value.getCoverData() == null
				|| value.getCoverData().isEmpty()) {
			context.buildConstraintViolationWithTemplate(
							"DIARY003")
					.addConstraintViolation();
			return false;
		}
		String imageUrl = value.getCoverData();
		if (!imageUrl.contains(".")) {
			context.buildConstraintViolationWithTemplate(
							"DIARY004")
					.addConstraintViolation();
			return false;
		}
		String fileExtension = imageUrl.substring(imageUrl.lastIndexOf("."));
		//		확장자 체크
		if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			context.buildConstraintViolationWithTemplate(
							"DIARY004")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	private boolean validateTitle(DiaryUpdateRequestDto value,
			ConstraintValidatorContext context) {
		if (value.getTitle() != null && !value.getTitle().isEmpty()) {
			if (value.getTitle().length() > TITLE_MAX_LENGTH) {
				context.buildConstraintViolationWithTemplate(
								"DIARY006")
						.addConstraintViolation();
				return false;
			}
		}
		return true;
	}

	private boolean validateGroupName(DiaryUpdateRequestDto value,
			ConstraintValidatorContext context) {
		if (value.getGroupName() != null && !value.getGroupName().isEmpty()) {
			if (value.getGroupName().length() > GROUP_NAME_MAX_LENGTH) {
				context.buildConstraintViolationWithTemplate(
								"DIARY008")
						.addConstraintViolation();
				return false;
			}
		}
		return true;
	}

	private boolean validateCoverType(DiaryUpdateRequestDto value,
			ConstraintValidatorContext context) {
		if (value.getCoverType() != null) {
			switch (value.getCoverType()) {
				case COLOR:
					return validateColorCover(value, context);
				case IMAGE:
					return validateImageCover(value, context);
			}
		}
		return true;
	}

	@Override
	public boolean isValid(DiaryUpdateRequestDto value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		return validateCoverType(value, context) && validateTitle(value, context)
				&& validateGroupName(value, context);
	}
}
