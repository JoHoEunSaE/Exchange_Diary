package org.johoeunsae.exchangediary.member.domain;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class MemberFeatures {
	private final String email;
	private final String nickname;

	private MemberFeatures(String email, String nickname) {
		this.email = email;
		this.nickname = nickname;
	}

	static public MemberFeatures of(String email, String nickname) {
		return new MemberFeatures(email, nickname);
	}
}
