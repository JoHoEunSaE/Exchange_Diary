package org.johoeunsae.exchangediary.auth.oauth2.vo;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Oauth2PublicKeys {

	private List<Oauth2PublicKey> keys;

	public Optional<Oauth2PublicKey> getMatchesKey(String alg, String kid) {
		return this.keys
				.stream()
				.filter(k -> k.getAlg().equals(alg) && k.getKid().equals(kid))
				.findFirst();
	}
}
