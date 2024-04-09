package org.johoeunsae.exchangediary.member.domain.policy;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MemberPolicyImpl implements MemberPolicy {
	private static final int LENGTH_NICKNAME = 10;
	private static final int LENGTH_STATEMENT = 30;
	private final static int NICKNAME_UPDATE_IMPOSSIBLE_DAYS = 7;

	@Override
	public String createRandomNickname() {
		return UUID.randomUUID().toString().substring(0, LENGTH_NICKNAME);
	}
	@Override
	public boolean isUpdatableNicknameDate(LocalDateTime lastNicknameUpdateDate, LocalDateTime now) {
		return lastNicknameUpdateDate.plusDays(NICKNAME_UPDATE_IMPOSSIBLE_DAYS).isBefore(now);
	}
	@Override
	public boolean isValidNickname(String nickname) {
		return nickname.length() <= LENGTH_NICKNAME;
	}
	@Override
	public boolean isValidStatement(String statement) {
		return statement.length() <= LENGTH_STATEMENT;
	}
}
