package org.johoeunsae.exchangediary.diary.domain.invitation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class InvitationManager {

	private final InvitationFactory invitationFactory;

	public Invitation createInvitation(Long diaryId, Long memberId,
	                                   InvitationType invitationType) {
		return invitationFactory.createInvitation(diaryId, memberId, invitationType).orElseThrow(IllegalArgumentException::new);
	}
}
