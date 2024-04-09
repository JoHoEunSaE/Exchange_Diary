package org.johoeunsae.exchangediary.auth.oauth2.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterReasonDTO;
import org.johoeunsae.exchangediary.cloud.aws.domain.SqsUnregisterMessage;

@Builder
@Getter
@ToString
@FieldNameConstants
public class MemberUnregisterEvent {

	private final UnregisterReasonDTO reason;

	public SqsUnregisterMessage toSqsMessage() {
		return SqsUnregisterMessage.builder()
				.title(reason.getReason())
				.content(reason.getOtherReason())
				.build();
	}
}
