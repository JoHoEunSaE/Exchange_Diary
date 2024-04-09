package org.johoeunsae.exchangediary.image.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.cloud.aws.domain.ObjectResourceManager;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.UtilsExceptionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	@Value("${spring.images.path.profile}")
	private String PROFILE_IMAGE_DIR;
	@Value("${spring.images.path.diary-cover}")
	private String DIARY_COVER_IMAGE_DIR;
	@Value("${spring.images.path.note}")
	private String NOTE_IMAGE_DIR;
	private final ObjectResourceManager objectResourceManager;
	private final List<String> validImageExtension = List.of(".jpg", ".jpeg", ".png");

	@Override
	public String getPreSignedUrl(String imageUrl) {
		log.info("Called getPreSignedUrl imageUrl: {}", imageUrl);
		if (!isValidDirName(imageUrl)) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_URL);
		}
		if (!isImageExtension(getExtension(imageUrl))) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		}
		return objectResourceManager.getPreSignedUrl(imageUrl);
	}

	private boolean isValidDirName(String imageUrl) {
		List<String> dirNameList = List.of(PROFILE_IMAGE_DIR, DIARY_COVER_IMAGE_DIR, NOTE_IMAGE_DIR);
		String dirName = getDirName(imageUrl);
		if (Objects.isNull(dirName) || dirName.isBlank())
			return false;
		return dirNameList.contains(dirName);
	}

	private String getDirName(String imageUrl) {
		if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
			return null;
		}
		return imageUrl.substring(0, imageUrl.indexOf("/") + 1);
	}

	@Override
	public String getImageUrl(String imageUrl) {
		if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
			return null;
		}
		return objectResourceManager.getObjectUrl(imageUrl);
	}

	@Override
	public void deleteImage(String imageUrl) {
		objectResourceManager.delete(imageUrl);
	}

	@Override
	public void validImageUrl(String imageUrl, String dirName) {
		log.info("Called validImageUrl imageUrl: {}, dirName: {}", imageUrl, dirName);
		// url이 유효하지 않은 형식이면 예외 발생
		if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_URL);
		}
		// 폴더가 유효하지 않으면 예외 발생
		if (!isInDirectory(imageUrl, dirName)) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_URL);
		}
		// 확장자가 지정한 이미지 확장자가 아니면 예외 발생
		if (!isImageExtension(getExtension(imageUrl))) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		}
	}

	@Override
	public String parseImageUrl(String imageUrl, String dirName) {
		log.info("Called parseImageUrl imageUrl: {}, dirName: {}", imageUrl, dirName);
		if (!imageUrl.contains(dirName))
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_URL);
		if (!isImageExtension(getExtension(imageUrl)))
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		String objectKey = imageUrl.substring(imageUrl.indexOf(dirName));
		if (!objectResourceManager.doesObjectExist(objectKey))
			return null;
		return objectKey;
	}

	private boolean isInDirectory(String imageUrl, String dirName) {
		return imageUrl.startsWith(dirName);
	}

	/**
	 * 올바른 이미지 확장자인지 검사한다.
	 *
	 * @param extension 확장자
	 * @return 올바른 이미지 확장자인지 여부
	 */
	private boolean isImageExtension(String extension) {
		return validImageExtension.contains(extension.toLowerCase());
	}

	private String getExtension(String filename) {
		if (!filename.contains("."))
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		return filename.substring(filename.lastIndexOf("."));
	}
}
