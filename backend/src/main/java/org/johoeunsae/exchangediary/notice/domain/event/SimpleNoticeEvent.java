package org.johoeunsae.exchangediary.notice.domain.event;

import static org.johoeunsae.exchangediary.notice.domain.NoticeAttribute.INNER_APP_NOTICE;
import static org.johoeunsae.exchangediary.notice.domain.NoticeAttribute.PUSH_NOTICE;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.notice.domain.NoticeAttribute;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

@Builder
@Getter
public class SimpleNoticeEvent implements NoticeEvent {
	private final String title;
	private final Long toId;
	@Builder.Default
	private final String format = "Test용 알림입니다.";
	@Builder.Default
	private final String deepLinkFormat = "Test용 알림입니다.";
	private final NoticeType noticeType = NoticeType.ANNOUNCEMENT;
	private final List<NoticeAttribute> attributes = List.of(PUSH_NOTICE, INNER_APP_NOTICE);

	@Override public List<String> getDeepLinkParameters() {
		return List.of();
	}
}
