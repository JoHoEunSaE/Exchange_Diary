package org.johoeunsae.exchangediary.auth.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder
@FieldNameConstants
public class JwtLoginTokenDto {

	private String accessToken;
}
