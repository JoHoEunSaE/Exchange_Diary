package org.johoeunsae.exchangediary.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtAuthenticationConverterTest {
	Jwt jwt;
	JwtAuthenticationConverter jwtAuthenticationConverter;

	@BeforeEach
	void setUp() throws Exception {
		jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwt = mock(Jwt.class);
	}

	@Test
	void roleConvert() throws Exception {
		// given
		List<String> roleList = List.of("test1", "test2", "test3");
		given(jwt.hasClaim(JwtProperties.ROLES)).willReturn(true);
		given(jwt.getClaimAsStringList(JwtProperties.ROLES)).willReturn(roleList);
		given(jwt.hasClaim(JwtProperties.SCOPES)).willReturn(false);

		// when
		JwtAuthenticationToken result = jwtAuthenticationConverter.convert(jwt);

		// then
		assertThat(result.getAuthorities()).map(Object::toString)
				.containsExactly("ROLE_test1", "ROLE_test2", "ROLE_test3");
		}

		@Test
		void scopeConvert() throws Exception {
			// given
			List<String> scopeList = List.of("test1", "test2", "test3");
			given(jwt.hasClaim(JwtProperties.ROLES)).willReturn(false);
			given(jwt.hasClaim(JwtProperties.SCOPES)).willReturn(true);
			given(jwt.getClaimAsStringList(JwtProperties.SCOPES)).willReturn(scopeList);

			// when
			JwtAuthenticationToken result = jwtAuthenticationConverter.convert(jwt);

			// then
			assertThat(result.getAuthorities()).map(Object::toString)
					.containsExactly("SCOPE_test1", "SCOPE_test2", "SCOPE_test3");
		}

		@Test
		void roleAndScopeConvert() throws Exception {
			// given
			List<String> roleList = List.of("testr1", "testr2", "testr3");
			List<String> scopeList = List.of("tests1", "tests2", "tests3");
			given(jwt.hasClaim(JwtProperties.ROLES)).willReturn(true);
			given(jwt.getClaimAsStringList(JwtProperties.ROLES)).willReturn(roleList);
			given(jwt.hasClaim(JwtProperties.SCOPES)).willReturn(true);
			given(jwt.getClaimAsStringList(JwtProperties.SCOPES)).willReturn(scopeList);

			// when
			JwtAuthenticationToken result = jwtAuthenticationConverter.convert(jwt);

			// then
			assertThat(result.getAuthorities()).map(Object::toString)
					.containsExactly("ROLE_testr1", "ROLE_testr2", "ROLE_testr3", "SCOPE_tests1", "SCOPE_tests2", "SCOPE_tests3");
		}
	}