package org.johoeunsae.exchangediary.member.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @ToString
public class PasswordInfo {
	private final String username;
	private final String password;

	private PasswordInfo(String username, String password) {
		this.username = username;
		this.password = password;
	}

	static public PasswordInfo createWithHash(String username, String password, PasswordEncoder passwordEncoder) {
		return new PasswordInfo(username, passwordEncoder.encode(password));
	}
}
