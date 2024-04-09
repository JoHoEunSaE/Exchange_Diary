package org.johoeunsae.exchangediary.notice.domain;

import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.test.UnitTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeviceRegistryTest extends UnitTest {

	@DisplayName("Member, DeviceToken, CreatedAt으로 DeviceRegistry를 생성할 수 있다.")
	@Test
	void of() {
		//given
		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		String deviceToken = "deviceToken";
		LocalDateTime createdAt = LocalDateTime.now();

		//when
		DeviceRegistry deviceRegistry = DeviceRegistry.of(member, deviceToken, createdAt);

		//then
		assertThat(deviceRegistry.getMember()).isEqualTo(member);
		assertThat(deviceRegistry.getToken()).isEqualTo(deviceToken);
		assertThat(deviceRegistry.getCreatedAt()).isEqualTo(createdAt);
	}

	@DisplayName("Member가 persist 되어 있지 않다면 DeviceRegistry를 생성할 수 없다.")
	@Test
	void fail_of() {
		//given
		Member member = mock(Member.class);
		when(member.getId()).thenReturn(null);
		String deviceToken = "deviceToken";
		LocalDateTime createdAt = LocalDateTime.now();

		//when, then
		assertThatThrownBy(() -> DeviceRegistry.of(member, deviceToken, createdAt))
				.isInstanceOf(DomainException.class);
	}
}