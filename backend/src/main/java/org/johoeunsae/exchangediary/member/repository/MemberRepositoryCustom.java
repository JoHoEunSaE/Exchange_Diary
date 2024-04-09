package org.johoeunsae.exchangediary.member.repository;

import org.johoeunsae.exchangediary.dto.MemberRelationDto;

public interface MemberRepositoryCustom {
	MemberRelationDto getMemberRelationDto(Long loginMemberId, Long memberId);
}
