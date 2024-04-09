package org.johoeunsae.exchangediary.cloud.aws.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.notice.domain.Message;

@Builder
@Getter
@ToString
public class SqsUnregisterMessage implements Message {

	private final String title;
	private final String content;
}
