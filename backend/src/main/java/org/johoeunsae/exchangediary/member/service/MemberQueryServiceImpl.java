package org.johoeunsae.exchangediary.member.service;

import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.NON_EXIST_DIARY;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.dto.DiaryMemberDto;
import org.johoeunsae.exchangediary.dto.DiaryMemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberRelationDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MemberUpdateDto;
import org.johoeunsae.exchangediary.dto.ProfileDto;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.follow.repository.FollowRepository;
import org.johoeunsae.exchangediary.follow.service.FollowQueryService;
import org.johoeunsae.exchangediary.mapper.MemberMapper;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.member.domain.SocialMember;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

	private final FollowRepository followRepository;
	private final FollowQueryService followService;
	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;
	private final DiaryRepository diaryRepository;
	private final RegistrationRepository registrationRepository;

	@Override
	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(() ->
				new ServiceException(MemberExceptionStatus.NOT_FOUND_MEMBER));
	}

	@Override
	public Optional<SocialMember> findSocialMemberByEmail(String email) {
		return memberRepository.findMemberByEmail(email)
				.filter(SocialMember.class::isInstance)
				.map(SocialMember.class::cast);
	}

	@Override
	public ProfileDto getMemberProfile(Long loginMemberId, Long memberId) {
		MemberRelationDto member = memberRepository.getMemberRelationDto(loginMemberId, memberId);
		if (member == null) {
			throw new ServiceException(MemberExceptionStatus.NOT_FOUND_MEMBER);
		}
		Integer followerCount = followRepository.countByToMemberId(memberId);
		Integer followingCount = followRepository.countByFromMemberId(memberId);
		return memberMapper.toProfileDto(member.getMember(), followerCount, followingCount,
				member.isFollowing(), member.isBlocked());
	}

	@Override
	public List<DiaryMemberPreviewDto> getMemberPreviewListInDairy(Long memberId, Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId).orElseThrow(NON_EXIST_DIARY::toServiceException);
		List<DiaryMemberDto> memberList = registrationRepository.getDiaryMembers(diaryId, memberId);
		List<DiaryMemberPreviewDto> memberPreviewList = memberList.stream()
				.map(m -> memberMapper.toDiaryMemberPreviewDto(m.getMember(),
						m.isFollowing(),
						m.isMaster(),
						m.isBlocked()))
				.collect(Collectors.toList());
		return sortMemberPreviewList(memberId, memberPreviewList);
	}

	@Override
	public MemberPreviewPaginationDto getMemberPreviewList(Long memberId, String searchNickname, Pageable pageable){
		Page<Member> findMembers = memberRepository.findByPartialNickname(searchNickname, pageable);
		List<MemberPreviewDto> memberToPreviewDto = findMembers.stream()
				.map(m -> memberMapper.toMemberPreviewDto(m,
						followService.isFollowing(memberId, m.getId())))
				.collect(Collectors.toList());
		return memberMapper.toMemberPreviewPaginationDto(findMembers.getTotalElements(), memberToPreviewDto);
	}

	@Override
	public OauthType getMemberOauthType(Long userId) {
		return memberRepository.findById(userId)
				.filter(SocialMember.class::isInstance)
				.map(SocialMember.class::cast)
				.map(SocialMember::getOauthType)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);
	}

	@Override
	public MemberUpdateDto getMemberUpdateDto(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);
		return memberMapper.toMemberUpdateDto(member);
	}

	private List<DiaryMemberPreviewDto> sortMemberPreviewList(Long loginMemberId,
			List<DiaryMemberPreviewDto> memberPreviewList) {
		Comparator<DiaryMemberPreviewDto> comparator = Comparator
				.comparing((DiaryMemberPreviewDto m) -> m.getMemberId().equals(loginMemberId),
						Comparator.reverseOrder())
				.thenComparing(DiaryMemberPreviewDto::isFollowing, Comparator.reverseOrder())
				.thenComparing(DiaryMemberPreviewDto::isBlocked)
				.thenComparing(DiaryMemberPreviewDto::getNickname);
		memberPreviewList.sort(comparator);
		return memberPreviewList;
	}
}
