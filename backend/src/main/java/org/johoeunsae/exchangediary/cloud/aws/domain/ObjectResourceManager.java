package org.johoeunsae.exchangediary.cloud.aws.domain;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectResourceManager {
	void upload(MultipartFile multipartFile, String key);
	void delete(String key);
	String getPreSignedUrl(String key);
	String getObjectUrl(String key);
	boolean doesObjectExist(String key);
}
