package org.johoeunsae.exchangediary.diary.domain.invitation.code;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.diary.domain.invitation.Invitation;
import org.johoeunsae.exchangediary.diary.domain.invitation.InvitationSupplier;
import org.johoeunsae.exchangediary.diary.domain.invitation.InvitationType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class CodeInvitationSupplier implements InvitationSupplier {


	@Override
	public boolean supports(InvitationType invitationType) {
		log.debug("Called supports invitationType = {}", invitationType);
		return InvitationType.CODE == invitationType;
	}

	@Override
	public Invitation supply(Long diaryId, Long memberId, InvitationType invitationType) {
		log.debug("Called supply diaryId: {}, memberId: {}, invitationType: {}", diaryId, memberId,
				invitationType);
		return CodeInvitation.of(diaryId);
	}
}
