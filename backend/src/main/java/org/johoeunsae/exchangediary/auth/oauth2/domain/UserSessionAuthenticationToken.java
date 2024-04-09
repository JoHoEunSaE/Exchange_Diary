package org.johoeunsae.exchangediary.auth.oauth2.domain;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserSessionAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;

	public UserSessionAuthenticationToken(
			Object principal,
			Collection<? extends GrantedAuthority> authorities
	) {
		super(authorities);
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		return principal;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
