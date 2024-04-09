package org.johoeunsae.exchangediary.member.domain.policy;

import java.time.LocalDateTime;

public interface MemberPolicy {
	String createRandomNickname();
	boolean isUpdatableNicknameDate(LocalDateTime lastNicknameUpdateDate, LocalDateTime now);

	boolean isValidNickname(String nickname);

	boolean isValidStatement(String statement);
}
