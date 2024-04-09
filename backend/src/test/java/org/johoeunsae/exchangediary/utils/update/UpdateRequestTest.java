package org.johoeunsae.exchangediary.utils.update;

import org.johoeunsae.exchangediary.utils.domain.IdDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import utils.test.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateRequestTest extends UnitTest {

	@Mock
	IdDomain<Integer> domain;

	TestRequest request;

	@BeforeEach
	void setUp() {
		request = new TestRequest();
	}

	@Test
	void apply() {
		request.register(domain);
		request.apply();
		assertThat(request.isSafe()).isTrue();
		assertThat(request.isRegistered()).isFalse();
		request.register(domain);
		request.addApplier();
		assertThat(request.isSafe()).isFalse();
		request.apply();
		assertThat(request.isSafe()).isTrue();
	}

	@Test
	void register() {
		assertThat(request.isRegistered()).isFalse();
		request.register(domain);
		assertThat(request.isRegistered()).isTrue();
	}

	@Test
	void validate() {
		request.register(domain);
		request.validate();
		assertThat(request.isValidated()).isTrue();
		request.addValidator();
		request.addValidator();
		request.addValidator();
		assertThat(request.isValidated()).isFalse();
		request.validate();
		assertThat(request.isValidated()).isTrue();
	}

	@Test
	void isValidated() {
		assertThat(request.isValidated()).isFalse();
		request.register(domain);
		assertThat(request.isValidated()).isTrue();
		request.addValidator();
		assertThat(request.isValidated()).isFalse();
		request.validate();
		assertThat(request.isValidated()).isTrue();
	}

	@Test
	void isValidatedAddApplier() {
		request.register(domain);
		request.addApplier();
		assertThat(request.isValidated()).isTrue();
	}

	@Test
	void isRegistered() {
		assertThat(request.isRegistered()).isFalse();
		request.register(domain);
		assertThat(request.isRegistered()).isTrue();
	}

	@Test
	void isSafe() {
		assertThat(request.isSafe()).isTrue();
		request.addValidator();
		assertThat(request.isSafe()).isTrue();
		request.register(domain);
		request.validate();
		assertThat(request.isSafe()).isFalse();
		request.apply();
		assertThat(request.isSafe()).isTrue();
		request.addApplier();
		assertThat(request.isSafe()).isFalse();
	}

	class TestRequest extends UpdateRequest<IdDomain<Integer>> {
		public void addValidator() {
			super.addValidator((domain) -> {
			}, (domain) -> {
			});
		}

		public void addApplier() {
			super.addApplier((domain) -> {
			});
		}
	}
}