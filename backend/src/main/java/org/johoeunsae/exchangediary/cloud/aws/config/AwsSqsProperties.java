package org.johoeunsae.exchangediary.cloud.aws.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AWS SQS 관련 정보를 담고 있는 Properties입니다.
 */
@Component
@Getter
public class AwsSqsProperties {
	@Value("${cloud.aws.sqs.queue.name}")
	private String queueName;
	@Value("${cloud.aws.sqs.queue.url}")
	private String queueUrl;
	@Value("${cloud.aws.sqs.queue.message-delay-seconds}")
	private Integer messageDelaySecs;
}
