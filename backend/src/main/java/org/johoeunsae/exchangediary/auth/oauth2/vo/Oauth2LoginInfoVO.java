package org.johoeunsae.exchangediary.auth.oauth2.vo;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.member.domain.OauthType;

@Getter
@Builder
public class Oauth2LoginInfoVO {
	private final String oauthId;
	private final String email;
	private final OauthType oauthType;
	private final String deviceToken;
	private final Optional<String> refreshToken;

	@Override public String toString() {
		return "Oauth2LoginInfoVO{" +
				"oauthId='" + oauthId + '\'' +
				", email='" + email + '\'' +
				", oauthType=" + oauthType +
				", deviceToken='" + deviceToken + '\'' +
				", refreshToken='" + refreshToken + '\\' +
				'}';
	}
}
