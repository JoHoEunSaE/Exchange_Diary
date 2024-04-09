package org.johoeunsae.exchangediary.cloud.aws.domain;

import com.amazonaws.services.s3.AmazonS3Client;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import utils.test.UnitTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AwsS3ManagerTest extends UnitTest {

	@Mock
	private final AmazonS3Client s3Client = mock(AmazonS3Client.class);
	private AwsS3Manager s3Manager;

	private String bucket;

	@BeforeEach
	void setUp() {
		bucket = "bucket";
		s3Manager = new AwsS3Manager(s3Client, bucket);
	}

	@DisplayName("MultipartFile(BLOB)을 해당하는 S3의 filePath에 업로드할 수 있다.")
	@Test
	void upload() {
		//given
		MultipartFile blob = mock(MultipartFile.class);
		when(blob.getContentType()).thenReturn("contentType");
		when(blob.getSize()).thenReturn(1L);
		String filePath = "filePath";

		//when
		s3Manager.upload(blob, filePath);

		//then
		then(s3Client).should().putObject(any());
	}

	@DisplayName("Amazon API가 실패할 시에 ServiceException을 던진다.")
	@Test
	void throwExceptionWhenAmazonRequestFail() {
		//given
		MultipartFile blob = mock(MultipartFile.class);
		when(blob.getContentType()).thenReturn("contentType");
		when(blob.getSize()).thenReturn(1L);
		String filePath = "filePath";
		when(s3Client.putObject(any())).thenThrow(ServiceException.class);

		//when, then
		assertThatThrownBy(() -> s3Manager.upload(blob, filePath))
				.isInstanceOf(ServiceException.class);
	}

	@DisplayName("S3에서 key값을 갖는 파일을 삭제할 수 있다.")
	@Test
	void delete() {
		//given
		String key = "fileKey";

		//when
		s3Manager.delete(key);

		//then
		then(s3Client).should().deleteObject(bucket, key);
	}

	@DisplayName("Amazon API가 실패할 시에 ServiceException을 던진다.")
	@Test
	void deleteAmazonApiFail() {
		//given
		willThrow(ServiceException.class).given(s3Client).deleteObject(any(), any());

		//when, then
		assertThatThrownBy(() -> s3Manager.delete("key"))
				.isInstanceOf(ServiceException.class);
	}
}