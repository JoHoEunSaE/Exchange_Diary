package org.johoeunsae.exchangediary.auth.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Jwt를 JwtAuthenticationToken으로 변환하는 컨버터
 */
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

	private static final String ROLE_PREFIX = "ROLE_";
	private static final String SCOPE_PREFIX = "SCOPE_";

	@Override
	public JwtAuthenticationToken convert(Jwt jwt) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (jwt.hasClaim(JwtProperties.ROLES)) {
			authorities.addAll(jwt.getClaimAsStringList(JwtProperties.ROLES).stream()
					.map(role -> ROLE_PREFIX + role)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList()));
		}
		if (jwt.hasClaim(JwtProperties.SCOPES)) {
			authorities.addAll(jwt.getClaimAsStringList(JwtProperties.SCOPES).stream()
					.map(scope -> SCOPE_PREFIX + scope)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList()));
		}
		return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
	}
}
