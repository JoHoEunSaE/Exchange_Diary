package org.johoeunsae.exchangediary.auth.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.auth.oauth2.domain.MemberUnregisterEvent;
import org.johoeunsae.exchangediary.cloud.aws.domain.AwsSqsManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Log4j2
@RequiredArgsConstructor
public class UnregisterEventHandler {

	private final AwsSqsManager awsSqsManager;


	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(MemberUnregisterEvent event) {
		log.info("handle event: {}", event);
		awsSqsManager.send(event.toSqsMessage());
	}

}
