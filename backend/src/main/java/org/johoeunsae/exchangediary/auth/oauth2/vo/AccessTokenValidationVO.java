package org.johoeunsae.exchangediary.auth.oauth2.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class AccessTokenValidationVO {

	@JsonProperty("resultcode")
	private String resultCode;

	@JsonProperty("message")
	private String message;
}
