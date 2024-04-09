package org.johoeunsae.exchangediary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.johoeunsae.exchangediary.member.domain.Member;

@NoArgsConstructor
@Getter
public class DiaryMemberDto {
	private Member member;
	private boolean isMaster;
	private boolean isBlocked;
	private boolean isFollowing;

	@Builder
	public DiaryMemberDto(Member member, boolean isMaster, boolean isBlocked, boolean isFollowing) {
		this.member = member;
		this.isMaster = isMaster;
		this.isBlocked = isBlocked;
		this.isFollowing = isFollowing;
	}

}
