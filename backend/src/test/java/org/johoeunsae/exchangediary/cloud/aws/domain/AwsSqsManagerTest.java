package org.johoeunsae.exchangediary.cloud.aws.domain;

import com.amazonaws.services.sqs.AmazonSQS;
import org.johoeunsae.exchangediary.cloud.aws.config.AwsSqsProperties;
import org.junit.jupiter.api.Disabled;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import utils.test.UnitTest;

import static org.mockito.Mockito.mock;

@Disabled
class AwsSqsManagerTest extends UnitTest {

	@InjectMocks
	private AwsSqsManager awsSqsManager;

	@Mock
	private AwsSqsProperties awsSqsProperties = mock(AwsSqsProperties.class);

	@Mock
	private AmazonSQS sqs = mock(AmazonSQS.class);

//	@DisplayName("SQS에 메시지를 전송할 수 있다.")
//	@Test
//	void sendMessageToQueue() {
//		//given
//		SendMessageRequest req = new SendMessageRequest("queueUrl", "messageBody");
//
//		//when
//		awsSqsManager.send(req);
//
//		//then
//		then(sqs).should().sendMessage(req);
//	}
//
//	@DisplayName("SqsMessageEvent를 전달받아 SQS에 전송할 푸시 알림 메시지 request를 생성할 수 있다.")
//	@Test
//	void createPushAlarmMessageRequest() {
//		//given
//		SqsMessageEvent event = SqsMessageEvent.builder("title", "content", "deviceToken")
//				.build();
//		when(awsSqsProperties.getQueueUrl()).thenReturn("queueUrl");
//		when(awsSqsProperties.getMessageDelaySecs()).thenReturn(0);
//
//		//when
//		SendMessageRequest req = awsSqsManager.createPushAlarmMessageRequest(event);
//
//		//then
//		then(awsSqsProperties).should().getQueueUrl();
//		then(awsSqsProperties).should().getMessageDelaySecs();
//		assertThat(req.getQueueUrl()).isEqualTo("queueUrl");
//		assertThat(req.getDelaySeconds()).isEqualTo(0);
//		assertThat(req.getMessageAttributes().get("title").getStringValue()).isEqualTo("title");
//		assertThat(req.getMessageAttributes().get("deviceToken").getStringValue()).isEqualTo("deviceToken");
//	}

}