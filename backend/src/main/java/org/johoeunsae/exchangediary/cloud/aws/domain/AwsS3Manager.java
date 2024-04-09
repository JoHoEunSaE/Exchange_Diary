package org.johoeunsae.exchangediary.cloud.aws.domain;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.CloudExceptionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Log4j2
public class AwsS3Manager implements ObjectResourceManager {

	private final String bucket;
	private final AmazonS3Client amazonS3Client;
	@Value("${spring.images.cloudfront-domain}")
	private String cloudfrontDomain;

	public AwsS3Manager(
			AmazonS3Client amazonS3Client,
			@Value("${cloud.aws.s3.bucket}") String bucket) {
		this.amazonS3Client = amazonS3Client;
		this.bucket = bucket;
	}

	@Override
	public void upload(MultipartFile multipartFile, String objectKey) {
		log.info("uploading file to s3: {}", objectKey);
		ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectKey,
					multipartFile.getInputStream(), objectMetadata);
			amazonS3Client.putObject(putObjectRequest);
		} catch (Exception e) {
			log.error("s3 upload error: {}", e.getMessage());
			e.printStackTrace();
			throw new ServiceException(CloudExceptionStatus.S3_UPLOAD_ERROR);
		}
	}

	private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
		ObjectMetadata objectMetadata = new ObjectMetadata();

		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());
		return objectMetadata;
	}

	public void delete(String key) {
		log.info("deleting file from s3: {}", key);
		try {
			amazonS3Client.deleteObject(bucket, key);
		} catch (Exception e) {
			log.error("s3 delete error: {}", e.getMessage());
			e.printStackTrace();
			throw new ServiceException(CloudExceptionStatus.S3_DELETE_ERROR);
		}
	}

	public String getObjectUrl(String key) {
		if (!doesObjectExist(key)) {
			log.warn("s3 object not found: {}", key);
			return null;
		}
		return cloudfrontDomain + "/" + key;
	}

	public String getPreSignedUrl(String objectKey) {
		GeneratePresignedUrlRequest request = createPreSignedUrlRequest(objectKey);
		return amazonS3Client.generatePresignedUrl(request).toString();
	}

	public boolean doesObjectExist(String objectKey) {
		return amazonS3Client.doesObjectExist(bucket, objectKey);
	}

	private GeneratePresignedUrlRequest createPreSignedUrlRequest(String objectKey) {
		return new GeneratePresignedUrlRequest(bucket, objectKey)
				.withMethod(HttpMethod.PUT)
				.withExpiration(getPreSignedUrlExpiration());
	}

	private Date getPreSignedUrlExpiration() {
		Date expriration = new Date();
		long expTimeMillis = expriration.getTime();
		expTimeMillis += 1000 * 60 * 60; // 1 hour
		expriration.setTime(expTimeMillis);
		return expriration;
	}
}
