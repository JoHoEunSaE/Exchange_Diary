package org.johoeunsae.exchangediary.cloud.aws.domain;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.cloud.aws.config.AwsSqsProperties;
import org.springframework.stereotype.Component;

/**
 * AWS SQS와 직접적으로 상호 작용하는 도메인 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class AwsSqsManager {

	private static final String NOTICE_TYPE = "NOTICE";
	private static final String UNREGISTER_TYPE = "UNREGISTER";
	private static final String TYPE_KEY = "type";
	private static final String STRING_TYPE = "String";

	private final AwsSqsProperties awsSqsProperties;
	private final AmazonSQS sqs;
	private final ObjectMapper jsonMapper;

	/**
	 * SQS에 메시지를 전송합니다.
	 *
	 * @param sqsNoticeMessage {@link SqsNoticeMessage}
	 * @return SQS에 전송한 메시지의 결과 {@link SendMessageResult}
	 * <p>
	 * 아래는 target이 되는 코틀린 데이터 포멧 data class NoticeEvent ( val receiverId: Long, val title: String?,
	 * val format: String, val attributes: List<NoticeAttribute>, val parameters:
	 * List<NoticeParameter>,
	 * @JsonDeserialize(using = LocalDateTimeDeserializer::class) val createdAt: LocalDateTime, ):
	 * EventTarget
	 */
	public void send(SqsNoticeMessage sqsNoticeMessage) {
		log.debug("send = {}", sqsNoticeMessage);
		try {
			String jsonMsg = jsonMapper.writeValueAsString(sqsNoticeMessage);
			SendMessageRequest request = createDefaultRequest(NOTICE_TYPE);
			request.setMessageBody(jsonMsg);
			SendMessageResult result = sqs.sendMessage(request);
			log.debug("send result = {}", result);
		} catch (JsonProcessingException e) {
			log.fatal("SqsMessage JSON 변환, 메시징에 실패했습니다. - 실패 객체 {}, {}", sqsNoticeMessage, e);
			throw new IllegalArgumentException("message가 json으로 변환되지 않습니다.");
		}
	}

	public void send(SqsUnregisterMessage sqsUnregisterMessage) {
		log.debug("send = {}", sqsUnregisterMessage);
		try {
			String jsonMsg = jsonMapper.writeValueAsString(sqsUnregisterMessage);
			SendMessageRequest request = createDefaultRequest(UNREGISTER_TYPE);
			request.setMessageBody(jsonMsg);
			SendMessageResult result = sqs.sendMessage(request);
			log.debug("send result = {}", result);
		} catch (JsonProcessingException e) {
			log.fatal("SqsMessage JSON 변환, 메시징에 실패했습니다. - 실패 객체 {}, {}", sqsUnregisterMessage, e);
			throw new IllegalArgumentException("message가 json으로 변환되지 않습니다.");
		}
	}

	private SendMessageRequest createDefaultRequest(String messageType) {
		MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
				.withDataType(STRING_TYPE)
				.withStringValue(messageType);
		Map<String, MessageAttributeValue> type = Map.of(TYPE_KEY, messageAttributeValue);

		return new SendMessageRequest()
				.withQueueUrl(awsSqsProperties.getQueueUrl())
				.withDelaySeconds(awsSqsProperties.getMessageDelaySecs())
				.withMessageAttributes(type);
	}
}
