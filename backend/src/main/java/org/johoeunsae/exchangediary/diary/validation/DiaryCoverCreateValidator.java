package org.johoeunsae.exchangediary.diary.validation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.johoeunsae.exchangediary.dto.DiaryCreateRequestDto;

public class DiaryCoverCreateValidator implements
		ConstraintValidator<DiaryCoverCreateValidation, DiaryCreateRequestDto> {

	private static final String COLOR_COVER_REGEX = "^#[0-9a-fA-F]{8}";
	private static final Pattern COLOR_COVER_PATTERN = Pattern.compile(COLOR_COVER_REGEX);
	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
			".jpg", ".jpeg", ".png"
	);

	private boolean validateColorCover(DiaryCreateRequestDto value,
			ConstraintValidatorContext context) {
//		값 유무 체크
		if (value.getCoverData() == null || value.getCoverData().isEmpty()) {
			context.buildConstraintViolationWithTemplate(
							"DIARY001")
					.addConstraintViolation();
			return false;
		}
//		문법 오류 체크
		if (!COLOR_COVER_PATTERN.matcher(value.getCoverData()).matches()) {
			context.buildConstraintViolationWithTemplate(
							"DIARY002")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	private boolean validateImageCover(DiaryCreateRequestDto value,
			ConstraintValidatorContext context) {
//		값 유무 체크
		if (value.getCoverData() == null
				|| value.getCoverData().isEmpty()) {
			context.buildConstraintViolationWithTemplate(
							"DIARY003")
					.addConstraintViolation();
			return false;
		}
		//		확장자 체크
		String imageUrl = value.getCoverData();
		if (!imageUrl.contains(".")) {
			context.buildConstraintViolationWithTemplate(
							"DIARY004")
					.addConstraintViolation();
			return false;
		}
		String fileExtension = imageUrl.substring(imageUrl.lastIndexOf("."));
		if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			context.buildConstraintViolationWithTemplate(
							"DIARY004")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid(DiaryCreateRequestDto value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();

		switch (value.getCoverType()) {
			case COLOR:
				return validateColorCover(value, context);
			case IMAGE:
				return validateImageCover(value, context);
			default:
				return true;
		}
	}
}
