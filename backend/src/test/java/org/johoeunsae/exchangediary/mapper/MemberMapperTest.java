package org.johoeunsae.exchangediary.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.johoeunsae.exchangediary.dto.AuthorDto;
import org.johoeunsae.exchangediary.dto.BlockedUserDto;
import org.johoeunsae.exchangediary.dto.DiaryMemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.MemberUpdateDto;
import org.johoeunsae.exchangediary.dto.ProfileDto;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import utils.test.UnitTest;
import utils.testdouble.member.TestMember;

public class MemberMapperTest extends UnitTest {
	@Mock
	private ImageService imageService;

	@InjectMocks
	private MemberMapper memberMapper = Mappers.getMapper(MemberMapper.class);

	private Member testMember;

	private static final String MOCKED_IMAGE_URL = "http://image-url/profile-images/1.jpg";

	@BeforeEach
	void setUp() {
		testMember = TestMember.asDefaultEntity();
		testMember.updateProfileImageUrl("1.jpg");

		when(imageService.getImageUrl(anyString())).thenReturn(MOCKED_IMAGE_URL);
	}

	@Test
	public void testToProfileDto() {
		// given
		int followerCount = 10;
		int followingCount = 10;
		boolean isFollowing = true;
		boolean isBlocked = true;

		// when
		ProfileDto result = memberMapper.toProfileDto(testMember, followerCount, followingCount,
				isFollowing, isBlocked);

		// then
		verify(imageService, times(1)).getImageUrl(anyString());

		assertEquals(testMember.getId(), result.getMemberId());
		assertEquals(MOCKED_IMAGE_URL, result.getProfileImageUrl());
		assertEquals(followerCount, result.getFollowerCount());
		assertEquals(followingCount, result.getFollowingCount());
		assertEquals(isFollowing, result.getIsFollowing());
		assertEquals(isBlocked, result.getIsBlocked());
	}

	@Test
	public void testToMemberPreviewDto() {
		// given
		boolean isFollowing = true;

		// when
		MemberPreviewDto result = memberMapper.toMemberPreviewDto(testMember, isFollowing);

		// then
		assertEquals(testMember.getId(), result.getMemberId());
		assertEquals(MOCKED_IMAGE_URL, result.getProfileImageUrl());
		assertEquals(isFollowing, result.isFollowing());
	}

	@Test
	public void testToDiaryMemberPreviewDto() {
		// given
		boolean isFollowing = true;
		boolean isMaster = true;
		boolean isBlocked = true;

		// when
		DiaryMemberPreviewDto result = memberMapper.toDiaryMemberPreviewDto(testMember, isFollowing
				, isMaster, isBlocked);

		// then
		assertEquals(testMember.getId(), result.getMemberId());
		assertEquals(MOCKED_IMAGE_URL, result.getProfileImageUrl());
		assertEquals(isFollowing, result.isFollowing());
		assertEquals(isMaster, result.isMaster());
		assertEquals(isBlocked, result.isBlocked());
	}

	@Test
	public void testToAuthorDto() {
		// given

		// when
		AuthorDto result = memberMapper.toAuthorDto(testMember);

		// then
		assertEquals(testMember.getId(), result.getMemberId());
		assertEquals(MOCKED_IMAGE_URL, result.getProfileImageUrl());
	}

	@Test
	public void testToBlockedUserDto() {
		// given

		// when
		BlockedUserDto result = memberMapper.toBlockedUserDto(testMember);

		// then
		assertEquals(testMember.getId(), result.getBlockedUserId());
		assertEquals(MOCKED_IMAGE_URL, result.getProfileImageUrl());
	}

	@Test
	public void testToMemberUpdateDto() {
		// given

		// when
		MemberUpdateDto result = memberMapper.toMemberUpdateDto(testMember);

		// then
		assertEquals(testMember.getId(), result.getMemberId());
		assertEquals(MOCKED_IMAGE_URL, result.getProfileImageUrl());
	}
}
