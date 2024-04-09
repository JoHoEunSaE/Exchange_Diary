package org.johoeunsae.exchangediary.auth.oauth2.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class NaverLoginInfoVO {

	@JsonProperty("resultcode")
	private final String resultCode;

	@JsonProperty("message")
	private final String message;

	@JsonProperty("response")
	private final ResponseDetail response;

	@Getter
	@AllArgsConstructor
	@ToString
	public static class ResponseDetail {

		@JsonProperty("id")
		private final String oauthId;

		@JsonProperty("email")
		private final String email;
	}
}
