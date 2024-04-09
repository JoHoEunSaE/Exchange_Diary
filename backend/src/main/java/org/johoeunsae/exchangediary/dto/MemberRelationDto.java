package org.johoeunsae.exchangediary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.johoeunsae.exchangediary.member.domain.Member;

@NoArgsConstructor
@Getter
public class MemberRelationDto {
	private Member member;
	private boolean isBlocked;
	private boolean isFollowing;

	@Builder
	public MemberRelationDto(Member member, boolean isBlocked, boolean isFollowing) {
		this.member = member;
		this.isBlocked = isBlocked;
		this.isFollowing = isFollowing;
	}
}
