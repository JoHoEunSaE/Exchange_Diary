package org.johoeunsae.exchangediary.member.domain;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class OauthInfo {
	private final String oauthId;
	private final OauthType oauthType;
	private OauthInfo(String oauthId, OauthType oauthType) {
		this.oauthId = oauthId;
		this.oauthType = oauthType;
	}
	static public OauthInfo of(String oauthId, OauthType oauthType) {
		return new OauthInfo(oauthId, oauthType);
	}
}
