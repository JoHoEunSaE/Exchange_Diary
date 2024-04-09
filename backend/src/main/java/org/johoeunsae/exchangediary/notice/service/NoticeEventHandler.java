package org.johoeunsae.exchangediary.notice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.cloud.aws.domain.AwsSqsManager;
import org.johoeunsae.exchangediary.cloud.aws.domain.SqsNoticeMessage;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.notice.domain.Message;
import org.johoeunsae.exchangediary.notice.domain.NoticeEventResolver;
import org.johoeunsae.exchangediary.notice.domain.event.NoticeEvent;
import org.johoeunsae.exchangediary.notice.repository.DeviceRegistryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Log4j2
public class NoticeEventHandler {

	private final DiaryRepository diaryRepository;
	private final MemberRepository memberRepository;
	private final DeviceRegistryRepository deviceRegistryRepository;

	private final NoticeEventResolver noticeEventResolver;
	private final AwsSqsManager awsSqsManager; // TODO : 다른 MQ나 외부 API로 변경될 수 있음.

	/**
	 * Service에서는 해당 Event에 대한 정보(DTO)만 넘긴다. <- 이 때의 Event는 NoticeEvent의 구현체다. NoticeEvent에 상수로
	 * 메타데이터를 지정해준다. (ex. NoticeEvent.NOTICE_TYPE_PUSH) 해당 Event를 처리하는 객체에서 어떠한 메타데이터(푸시 알림, 앱 내 알림,
	 * 알림 내용 등)를 생성할지 결정한다. 이 때, Event 처리 객체는 Handler에서 주입받는다.
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(NoticeEvent event) {
		log.debug("handle = {}", event);

		// NoticeEvent를 Message로 변환하는 Convert 객체를 이용하여 Message를 생성한다.
		List<Message> message = noticeEventResolver.convert(event);
		// 생성된 Message를 MQ에 전송한다.
		// TODO : 메시지 타입에 따라 알맞은 MessageQueue에 어댑팅해주는 객체 주입 필요
		message.forEach(m -> awsSqsManager.send((SqsNoticeMessage) m));
	}
}
