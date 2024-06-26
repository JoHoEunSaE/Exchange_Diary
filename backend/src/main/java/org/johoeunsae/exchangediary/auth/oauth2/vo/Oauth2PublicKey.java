package org.johoeunsae.exchangediary.auth.oauth2.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Oauth2PublicKey {

	private String kty;
	private String kid;
	private String use;
	private String alg;
	private String n;
	private String e;
}
