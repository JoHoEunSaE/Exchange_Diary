package org.johoeunsae.exchangediary.auth.oauth2.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.member.domain.MemberRole;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class UserSessionDto {

	private final Long userId;
	private final List<MemberRole> roles;
}
