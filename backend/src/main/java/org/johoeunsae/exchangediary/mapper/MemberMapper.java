package org.johoeunsae.exchangediary.mapper;

import java.util.List;
import org.johoeunsae.exchangediary.dto.AuthorDto;
import org.johoeunsae.exchangediary.dto.BlockedUserDto;
import org.johoeunsae.exchangediary.dto.DiaryMemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MemberUpdateDto;
import org.johoeunsae.exchangediary.dto.ProfileDto;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MemberMapper {

	@Autowired
	protected ImageService imageService;

	@Named("mapImageUrl")
	protected String mapImageUrl(String profileImageUrl) {
		return imageService.getImageUrl(profileImageUrl);
	}

	@Mapping(source = "member.id", target = "memberId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract ProfileDto toProfileDto(Member member, int followerCount, int followingCount,
			boolean isFollowing, boolean isBlocked);

	@Mapping(source = "member.id", target = "memberId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract MemberPreviewDto toMemberPreviewDto(Member member, boolean isFollowing);

	@Mapping(source = "member.id", target = "memberId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract DiaryMemberPreviewDto toDiaryMemberPreviewDto(Member member, boolean isFollowing,
			boolean isMaster, boolean isBlocked);

	public abstract MemberPreviewPaginationDto toMemberPreviewPaginationDto(Long totalLength, List<MemberPreviewDto> result);

	@Mapping(source = "member.id", target = "memberId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract AuthorDto toAuthorDto(Member member);

	@Mapping(source = "member.id", target = "blockedUserId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract BlockedUserDto toBlockedUserDto(Member member);

	@Mapping(source = "member.id", target = "memberId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract MemberUpdateDto toMemberUpdateDto(Member member);

	@Mapping(source = "memberPreviewDtos", target = "result")
	public abstract MemberPreviewPaginationDto toMemberPreviewPaginationDto(
			List<MemberPreviewDto> memberPreviewDtos, long totalLength);
}
