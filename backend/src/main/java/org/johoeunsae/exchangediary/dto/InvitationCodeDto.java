package org.johoeunsae.exchangediary.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class InvitationCodeDto {

	private final String invitationCode;
	private final LocalDateTime expiredAt;
}
