package org.johoeunsae.exchangediary.cloud.aws.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * AWS 인스턴스를 Bean으로 등록하는 Config입니다.
 * <p>
 * 현재 sichoi님 계정의 mini-sanan IAM을 기준으로 사용합니다 - SQS, Lambda를 통한 알람
 */
@Configuration
public class AwsConfig {
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;
	@Value("${cloud.aws.region.static}")
	private String region;

	/**
	 * AWS 인증 정보를 담고 있는 객체를 생성합니다.
	 *
	 * @return {@link AWSStaticCredentialsProvider}
	 */
	private AWSStaticCredentialsProvider createAwsCredentialsProvider() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		return new AWSStaticCredentialsProvider(basicAWSCredentials);
	}

	/**
	 * SQS Client를 생성, 빈으로 등록합니다.
	 *
	 * @return {@link AmazonSQS}
	 */
	@Bean
	public AmazonSQS amazonSQS() {
		return AmazonSQSAsyncClient.asyncBuilder()
				.withRegion(region)
				.withCredentials(createAwsCredentialsProvider())
				.build();
	}

	/**
	 * S3 Client를 생성, 빈으로 등록합니다.
	 *
	 * @return {@link AmazonS3Client}
	 */
	@Bean
	public AmazonS3Client amazonS3Client() {
		return (AmazonS3Client) AmazonS3Client.builder()
				.withRegion(region)
				.withCredentials(createAwsCredentialsProvider())
				.build();
	}
}
