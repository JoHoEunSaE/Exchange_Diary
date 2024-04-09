package org.johoeunsae.exchangediary.auth.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

public class JwtTokenProviderTest {
	JwtTokenProvider jwtTokenProvider;
	JwtEncoder encoder;

	@BeforeEach
	void setUp() throws Exception {
		String key = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		encoder = new NimbusJwtEncoder(
				new ImmutableSecret<>(
				   new SecretKeySpec(key.getBytes(),
						MacAlgorithm.HS256.getName())));
		jwtTokenProvider = new JwtTokenProvider(encoder);
	}

	@Test
	void createCommonAccessToken() throws Exception {
		// given
		Long id = 1L;

		// when
		Jwt accessToken = jwtTokenProvider.createCommonAccessToken(id);

		// then
		assertThat(accessToken.getClaims().get(JwtProperties.ROLES)).asList().contains(MemberRole.USER.name());
		assertThat(accessToken.getClaims().get(JwtProperties.USER_ID)).isEqualTo(id);
	}

}
