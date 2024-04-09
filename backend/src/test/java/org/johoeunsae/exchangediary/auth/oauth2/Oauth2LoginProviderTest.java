package org.johoeunsae.exchangediary.auth.oauth2;

import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2Login;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginFactory;
import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import utils.test.UnitTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class Oauth2LoginProviderTest extends UnitTest {
	@Mock private Oauth2Login falseLogin;
	@Mock private Oauth2Login trueLogin;
	@Mock private Oauth2LoginSupplier falseSupplier;
	@Mock private Oauth2LoginSupplier trueSupplier;
	private Oauth2LoginFactory factory;

	@BeforeEach
	void setUp() {
		factory = new Oauth2LoginFactory(null);
	}


	@DisplayName("Oauth2LoginStrategyProviderTest")
	@Test
	void 정상적으로_전략이_들어간_경우() throws Exception {
		// given
		given(trueLogin.isValid()).willReturn(true);
		given(falseSupplier.supports(any())).willReturn(false);
		given(trueSupplier.supports(any())).willReturn(true);
		given(trueSupplier.supply(any())).willReturn(trueLogin);
		Oauth2LoginFactory falseTrueFactory = new Oauth2LoginFactory(null);
		Oauth2LoginFactory trueFalseFactory = new Oauth2LoginFactory(null);
		falseTrueFactory.addSuppliers(List.of(falseSupplier, trueSupplier));
		trueFalseFactory.addSuppliers(List.of(trueSupplier, falseSupplier));

		// when
		Optional<Oauth2Login> oauth2Login = falseTrueFactory.create(null);
		Optional<Oauth2Login> oauth2Login2 = trueFalseFactory.create(null);

		// then
		assertThat(oauth2Login.map(Oauth2Login::isValid).orElse(false)).isTrue();
		assertThat(oauth2Login2.map(Oauth2Login::isValid).orElse(false)).isTrue();
	}

	@DisplayName("Oauth2LoginStrategyProviderTest")
	@Test
	void 정상적으로_전략이_없는_경우() throws Exception {
		assertThat(factory.create(null)).isEmpty();
	}

	@DisplayName("Oauth2LoginStrategyProviderTest")
	@Test
	void false_전략이_있는_경우() throws Exception {
		//given
		given(falseSupplier.supports(any())).willReturn(false);

		//when
		factory.addSuppliers(List.of(falseSupplier));
		Optional<Oauth2Login> oauth2Login = factory.create(null);

		//then
		assertThat(oauth2Login).isEmpty();
	}
}