package org.johoeunsae.exchangediary.auth.jwt.excpetion;

public class JwtAuthenticationTokenException extends RuntimeException {
	public JwtAuthenticationTokenException(String s) {
		super(s);
	}
}
