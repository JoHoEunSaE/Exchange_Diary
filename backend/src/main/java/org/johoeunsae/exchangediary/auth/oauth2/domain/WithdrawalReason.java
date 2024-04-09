package org.johoeunsae.exchangediary.auth.oauth2.domain;

import lombok.Getter;

@Getter
public enum WithdrawalReason {
	NO_LONGER_NEED_SERVICE("서비스가 필요하지 않게 되었어요."),
	TOO_HARD_TO_USE("이용이 어려웠어요."),
	USING_OTHER_SERVICE("다른 서비스를 사용하게 되었어요."),
	PERSONAL_INFO_CONCERN("개인 정보에 대한 걱정이 있었어요."),
	COST_EXPENSIVE("비용이 부담되었어요."),
	OTHER_REASON("기타 (자유롭게 작성해주세요.)");

	private final String reason;

	WithdrawalReason(String reason) {
		this.reason = reason;
	}

}
