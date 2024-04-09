package org.johoeunsae.exchangediary.member.domain.extension;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.Member;

@Getter @Builder
@ToString
public class MemberPass {
	public enum MemberType { NONE, SOCIAL, PASSWORD }
	private final Member member;
	private Oauth2LoginRequestVO loginDto;

	public MemberPass(Member member) { this.member = member; }
	protected MemberPass(Member member, Oauth2LoginRequestVO loginDto) {
		this.member = member;
		this.loginDto = loginDto;
	}

	public void setCode(Oauth2LoginRequestVO loginDto) { this.loginDto = loginDto; }

	public MemberType getMemberType() {
		String dtype = member.getDtype();
		if (dtype.equals(Member.SOCIAL_MEMBER) ) return MemberType.SOCIAL;
		else if (dtype.equals(Member.PASSWORD_MEMBER)) return MemberType.PASSWORD;
		return MemberType.NONE;
	}
}
