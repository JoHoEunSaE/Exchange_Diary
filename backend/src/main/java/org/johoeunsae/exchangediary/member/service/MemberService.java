package org.johoeunsae.exchangediary.member.service;

import java.time.LocalDateTime;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.dto.MemberUpdateRequestDto;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.SocialMember;

public interface MemberService {

	SocialMember createSocialMember(Oauth2LoginInfoVO vo);

	void updateProfile(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto);

	void deleteProfileImage(Long memberId);

	void deleteMember(Long memberId, Oauth2LoginRequestVO dto, LocalDateTime now);

	void upsertDeviceRegistry(Member member, String deviceToken, LocalDateTime now);
}
