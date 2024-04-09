package org.johoeunsae.exchangediary.dto.entity;

import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;

@Getter @ToString
public class MemberFromTo {
	private final Member from;
	private final Member to;
	private MemberFromTo(Member from, Member to) {
		this.from = from;
		this.to = to;
	}

	static public MemberFromTo of(Member from, Member to) {
		return new MemberFromTo(from, to);
	}

	public MemberCompositeKey asKey() {
		return MemberCompositeKey.of(from.getId(), to.getId());
	}
}
