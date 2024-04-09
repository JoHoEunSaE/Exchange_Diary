package org.johoeunsae.exchangediary.follow.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.johoeunsae.exchangediary.dto.MemberPreviewDto;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.follow.domain.Follow;
import org.johoeunsae.exchangediary.follow.repository.dto.MemberPrivacy;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.PasswordInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import utils.test.RepoTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class FollowQuerydslRepositoryImplTest extends RepoTest {

	@Autowired
	JPAQueryFactory queryFactory;
	FollowQuerydslRepositoryImpl repository;
	LocalDateTime now;
	Member member1;
	Member member2;
	Member member3;
	Member member4;
	Member member5;
	Pageable pageable;


	@BeforeEach
	void setUp() {
		repository = new FollowQuerydslRepositoryImpl(queryFactory);
		now = LocalDateTime.now();
		BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
		member1 = Member.createPasswordMember(MemberFeatures.of("member1@naver.com", "member1"),
				now,
				PasswordInfo.createWithHash("member1", "password", pe));
		member2 = Member.createPasswordMember(MemberFeatures.of("member2@naver.com", "member2"),
				now,
				PasswordInfo.createWithHash("member2", "password", pe));
		member3 = Member.createPasswordMember(MemberFeatures.of("member3@naver.com", "member3"),
				now,
				PasswordInfo.createWithHash("member3", "password", pe));
		member4 = Member.createPasswordMember(MemberFeatures.of("member4@naver.com", "member4"),
				now,
				PasswordInfo.createWithHash("member4", "password", pe));
		member5 = Member.createPasswordMember(MemberFeatures.of("member5@naver.com", "member5"),
				now,
				PasswordInfo.createWithHash("member5", "password", pe));

		pageable = PageRequest.of(0, 10);
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		em.persist(member5);
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("팔로우 조회 테스트")
	void findFollowerPrivacyListSimple() throws Exception {
		// given
		em.persist(Follow.of(MemberFromTo.of(member2, member1), now));
		em.persist(Follow.of(MemberFromTo.of(member3, member1), now));
		em.persist(Follow.of(MemberFromTo.of(member4, member1), now));
		em.persist(Follow.of(MemberFromTo.of(member5, member1), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPrivacy> followerPrivacyList = repository.findFollowerPrivacyList(
				member1.getId(), pageable);

		// then
		assertThat(followerPrivacyList.getTotalElements()).isEqualTo(4);
		assertThat(followerPrivacyList).contains(
				new MemberPrivacy(member2.getId(), member2.getNickname(),
						member2.getProfileImageUrl()),
				new MemberPrivacy(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl()),
				new MemberPrivacy(member4.getId(), member4.getNickname(),
						member4.getProfileImageUrl()),
				new MemberPrivacy(member5.getId(), member5.getNickname(),
						member5.getProfileImageUrl())
		);
	}

	@Test
	@DisplayName("결과가 없는 팔로우 조회 테스트")
	void findFollowerPrivacyListNull() throws Exception {
		// given
		// when
		Page<MemberPrivacy> followerPrivacyList = repository.findFollowerPrivacyList(
				member1.getId(), pageable);

		// then
		assertThat(followerPrivacyList).isEmpty();
	}

	@Test
	@DisplayName("팔로잉 조회 테스트")
	void findFollowingPrivacyListSimple() throws Exception {
		// given
		em.persist(Follow.of(MemberFromTo.of(member1, member2), now));
		em.persist(Follow.of(MemberFromTo.of(member1, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member1, member4), now));
		em.persist(Follow.of(MemberFromTo.of(member1, member5), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPrivacy> followingPrivacyList = repository.findFollowingPrivacyList(
				member1.getId(), pageable);

		// then
		assertThat(followingPrivacyList.getTotalElements()).isEqualTo(4);
		assertThat(followingPrivacyList).contains(
				new MemberPrivacy(member2.getId(), member2.getNickname(),
						member2.getProfileImageUrl()),
				new MemberPrivacy(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl()),
				new MemberPrivacy(member4.getId(), member4.getNickname(),
						member4.getProfileImageUrl()),
				new MemberPrivacy(member5.getId(), member5.getNickname(),
						member5.getProfileImageUrl())
		);
	}

	@Test
	@DisplayName("결과가 없는 팔로잉 조회 테스트")
	void findFollowingPrivacyListNull() throws Exception {
		// given
		// when
		Page<MemberPrivacy> followingPrivacyList = repository.findFollowingPrivacyList(
				member1.getId(), pageable);

		// then
		assertThat(followingPrivacyList).isEmpty();
	}

	@Test
	@DisplayName("로그인한 멤버의 팔로워 조회 테스트(로그인 한 멤버가 팔로우한 멤버)")
	void findFollowerPrivacyListForLoginTrue() throws Exception {
		// given
		// 1 -> 3, 3 -> 2
		Long loginId = member1.getId();
		Long memberId = member2.getId();
		em.persist(Follow.of(MemberFromTo.of(member1, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member3, member2), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPreviewDto> followerPrivacyList = repository.findFollowerPrivacyListForLogin(
				loginId, memberId, pageable);

		// then
		assertThat(followerPrivacyList.getTotalElements()).isEqualTo(1);
		assertThat(followerPrivacyList).contains(
				new MemberPreviewDto(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl(), true)
		);
	}

	@Test
	@DisplayName("로그인한 멤버의 팔로워 조회 테스트 (로그인 한 멤버가 팔로우한 멤버가 아님)")
	void findFollowerPrivacyListForLoginFalse() throws Exception {
		// given
		// 3 -> 2
		Long loginId = member1.getId();
		Long memberId = member2.getId();
		em.persist(Follow.of(MemberFromTo.of(member3, member2), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPreviewDto> followerPrivacyList = repository.findFollowerPrivacyListForLogin(
				loginId, memberId, pageable);

		// then
		assertThat(followerPrivacyList.getTotalElements()).isEqualTo(1);
		assertThat(followerPrivacyList).contains(
				new MemberPreviewDto(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl(), false)
		);
	}

	@Test
	@DisplayName("로그인한 멤버의 팔로워 조회 테스트 (로그인한 멤버가 팔로우한 멤버와 팔로우하지 않은 멤버가 섞여있음))")
	void findFollowerPrivacyListForLoginCombine() throws Exception {
		// given
		// 1 -> 3, 3 -> 2, 4 -> 2, 5 -> 1, 3 -> 5, 1 -> 5
		Long loginId = member1.getId();
		Long memberId = member2.getId();
		em.persist(Follow.of(MemberFromTo.of(member1, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member3, member2), now));
		em.persist(Follow.of(MemberFromTo.of(member4, member2), now));
		em.persist(Follow.of(MemberFromTo.of(member5, member1), now));
		em.persist(Follow.of(MemberFromTo.of(member3, member5), now));
		em.persist(Follow.of(MemberFromTo.of(member1, member5), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPreviewDto> followingPrivacyList = repository.findFollowerPrivacyListForLogin(
				loginId, memberId, pageable);

		// then
		assertThat(followingPrivacyList.getTotalElements()).isEqualTo(2);
		assertThat(followingPrivacyList).contains(
				new MemberPreviewDto(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl(), true),
				new MemberPreviewDto(member4.getId(), member4.getNickname(),
						member4.getProfileImageUrl(), false)
		);
	}

	@Test
	@DisplayName("찾는 멤버가 팔로워가 없음")
	void findFollowerPrivacyListForLoginNull() throws Exception {
		// given
		// when
		Page<MemberPreviewDto> followingPrivacyList = repository.findFollowerPrivacyListForLogin(
				member1.getId(), member2.getId(), pageable);

		// then
		assertThat(followingPrivacyList).isEmpty();
	}

	@Test
	@DisplayName("로그인한 멤버의 팔로잉 조회 테스트 (로그인 한 멤버가 팔로우한 멤버)")
	void findFollowingPrivacyListForLoginTrue() throws Exception {
		// given
		// 1 -> 3, 2 -> 3
		Long loginId = member1.getId();
		Long memberId = member2.getId();
		em.persist(Follow.of(MemberFromTo.of(member1, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member2, member3), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPreviewDto> followingPrivacyList = repository.findFollowingPrivacyListForLogin(
				loginId, memberId, pageable);

		// then
		assertThat(followingPrivacyList.getTotalElements()).isEqualTo(1);
		assertThat(followingPrivacyList).contains(
				new MemberPreviewDto(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl(), true)
		);
	}

	@Test
	@DisplayName("로그인한 멤버의 팔로잉 조회 테스트 (로그인 한 멤버가 팔로우한 멤버가 아님)")
	void findFollowingPrivacyListForLoginFalse() throws Exception {
		// given
		// 2 -> 3
		Long loginId = member1.getId();
		Long memberId = member2.getId();
		em.persist(Follow.of(MemberFromTo.of(member2, member3), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPreviewDto> followingPrivacyList = repository.findFollowingPrivacyListForLogin(
				loginId, memberId, pageable);

		// then
		assertThat(followingPrivacyList.getTotalElements()).isEqualTo(1);
		assertThat(followingPrivacyList).contains(
				new MemberPreviewDto(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl(), false)
		);
	}

	@Test
	@DisplayName("로그인한 멤버의 팔로잉 조회 테스트 (로그인한 멤버가 팔로우한 멤버와 팔로우하지 않은 멤버가 섞여있음))")
	void findFollowingPrivacyListForLoginCombine() throws Exception {
		// given
		// 1 -> 3, 2 -> 3, 2 -> 4, 1 -> 5, 5 -> 3, 5 -> 1
		Long loginId = member1.getId();
		Long memberId = member2.getId();
		em.persist(Follow.of(MemberFromTo.of(member1, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member2, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member2, member4), now));
		em.persist(Follow.of(MemberFromTo.of(member1, member5), now));
		em.persist(Follow.of(MemberFromTo.of(member5, member3), now));
		em.persist(Follow.of(MemberFromTo.of(member5, member1), now));
		em.flush();
		em.clear();

		// when
		Page<MemberPreviewDto> followingPrivacyList = repository.findFollowingPrivacyListForLogin(
				loginId, memberId, pageable);

		// then
		assertThat(followingPrivacyList.getTotalElements()).isEqualTo(2);
		assertThat(followingPrivacyList).contains(
				new MemberPreviewDto(member3.getId(), member3.getNickname(),
						member3.getProfileImageUrl(), true),
				new MemberPreviewDto(member4.getId(), member4.getNickname(),
						member4.getProfileImageUrl(), false)
		);
	}

	@Test
	@DisplayName("찾는 멤버가 팔로잉이 없음")
	void findFollowingPrivacyListForLoginNull() throws Exception {
		// given
		// when
		Page<MemberPreviewDto> followingPrivacyList = repository.findFollowingPrivacyListForLogin(
				member1.getId(), member2.getId(), pageable);

		// then
		assertThat(followingPrivacyList).isEmpty();
	}
}
