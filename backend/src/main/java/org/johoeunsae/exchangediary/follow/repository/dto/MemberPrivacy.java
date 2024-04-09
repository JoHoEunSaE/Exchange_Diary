package org.johoeunsae.exchangediary.follow.repository.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor @Getter
@EqualsAndHashCode
public class MemberPrivacy {
	private final Long memberId;
	private final String nickname;
	private final String profileImageUrl;
}
