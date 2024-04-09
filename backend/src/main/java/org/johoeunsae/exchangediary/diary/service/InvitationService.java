package org.johoeunsae.exchangediary.diary.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.diary.domain.invitation.Invitation;
import org.johoeunsae.exchangediary.diary.domain.invitation.code.CodeInvitation;
import org.johoeunsae.exchangediary.redis.service.RedisService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.INVALID_INVITATION_CODE;

@Service
@RequiredArgsConstructor
public class InvitationService {

	private final RedisService redisService;

	public Optional<CodeInvitation> findByDiaryId(Long diaryId) {
		String invitationDiaryIdKey = invitationDiaryIdKey(diaryId);
		return redisService.findByKey(invitationDiaryIdKey, CodeInvitation.class);
	}

	public CodeInvitation getByInvitationCode(String invitationCode) {
		String invitationCodeKey = invitationCodeKey(invitationCode);
		return redisService.findByKey(invitationCodeKey, CodeInvitation.class)
				.orElseThrow(INVALID_INVITATION_CODE::toServiceException);
	}


	public Invitation saveInvitation(Invitation invitation) {
		Duration ttl = invitation.getRemainDuration(LocalDateTime.now());

		String invitationDiaryIdKey = invitationDiaryIdKey(invitation.getDiaryId());
		String invitationCodeKey = invitationCodeKey(invitation.getValue());

		redisService.save(invitationDiaryIdKey, invitation, ttl);
		redisService.save(invitationCodeKey, invitation, ttl);
		return invitation;
	}

	public void deleteInvitation(Invitation invitation) {
		String invitationDiaryIdKey = invitationDiaryIdKey(invitation.getDiaryId());
		String invitationCodeKey = invitationCodeKey(invitation.getValue());

		redisService.delete(invitationDiaryIdKey);
		redisService.delete(invitationCodeKey);
	}


	/**
	 * DiaryId -> Invitation 조회를 위한 키
	 */
	private String invitationDiaryIdKey(Long diaryId) {
		return "invitation:diaryId:" + diaryId;
	}

	/**
	 * InvitationCode -> Invitation 조회를 위한 키
	 */
	private String invitationCodeKey(final String invitationCode) {
		return "invitation:code:" + invitationCode;
	}
}
