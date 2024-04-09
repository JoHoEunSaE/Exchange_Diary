package org.johoeunsae.exchangediary.diary.domain.invitation;

public interface InvitationSupplier {

	boolean supports(InvitationType invitationType);

	Invitation supply(Long diaryId, Long memberId, InvitationType invitationType);
}
