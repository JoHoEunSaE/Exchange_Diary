package org.johoeunsae.exchangediary.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.dto.ImageRequestDto;
import org.johoeunsae.exchangediary.exception.annotation.ApiErrorCodeExample;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.UtilsExceptionStatus;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.log.Logging;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이미지", description = "이미지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/images")
@Logging
public class ImageController {

	private final ImageService imageService;

	@PostMapping("/presigned-url")
	@Operation(summary = "이미지 presigned-url 발급", description = "이미지 업로드를 위한 presigned-url을 발급합니다.")
	@ApiErrorCodeExample(
			authExceptionStatuses = {AuthExceptionStatus.UNAUTHORIZED_MEMBER},
			utilsExceptionStatuses = {
					UtilsExceptionStatus.INVALID_FILE,
					UtilsExceptionStatus.INVALID_FILE_EXTENSION,
					UtilsExceptionStatus.INVALID_FILE_URL
			}
	)
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	public String getPreSignedUrl(
			@RequestBody ImageRequestDto imageRequestDto
	) {
		return imageService.getPreSignedUrl(imageRequestDto.getImageUrl());
	}
}
