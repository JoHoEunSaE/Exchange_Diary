package org.johoeunsae.exchangediary.cloud.aws.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.notice.domain.Message;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;


@Builder
@Getter
@ToString
public class SqsNoticeMessage implements Message {

	private final Long receiverId;
	private final NoticeType noticeType;
	private final String title;
	private final String format;
	private final List<String> attributes;
	private final List<String> parameters;
	private final LocalDateTime createdAt;
}
