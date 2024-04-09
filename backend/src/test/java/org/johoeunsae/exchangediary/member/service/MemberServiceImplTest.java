package org.johoeunsae.exchangediary.member.service;

import org.johoeunsae.exchangediary.member.domain.policy.MemberPolicy;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.notice.repository.DeviceRegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import utils.test.UnitTest;


class MemberServiceImplTest extends UnitTest {
	private MemberServiceImpl memberServiceImpl;

	@BeforeEach
	void setUp(
			@Mock MemberRepository memberRepository,
			@Mock MemberPolicy memberPolicy,
			@Mock DeviceRegistryRepository deviceRegistryRepository
	) {
	}
}