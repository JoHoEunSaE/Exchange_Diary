package org.johoeunsae.exchangediary.member.service;

import java.util.List;
import java.util.Optional;
import org.johoeunsae.exchangediary.dto.DiaryMemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MemberUpdateDto;
import org.johoeunsae.exchangediary.dto.ProfileDto;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.member.domain.SocialMember;
import org.springframework.data.domain.Pageable;

public interface MemberQueryService {
	Member getMember(Long memberId);
	Optional<SocialMember> findSocialMemberByEmail(String email);
	ProfileDto getMemberProfile(Long loginMemberId, Long memberId);
	List<DiaryMemberPreviewDto> getMemberPreviewListInDairy(Long memberId, Long diaryId);
	MemberPreviewPaginationDto getMemberPreviewList(Long memberId, String searchNickname, Pageable pageable);
	OauthType getMemberOauthType(Long userId);
	MemberUpdateDto getMemberUpdateDto(Long memberId);
}
