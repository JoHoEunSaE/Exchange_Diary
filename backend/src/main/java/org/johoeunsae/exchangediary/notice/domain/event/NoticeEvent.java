package org.johoeunsae.exchangediary.notice.domain.event;

import org.johoeunsae.exchangediary.notice.domain.NoticeAttribute;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 알림 이벤트를 구현하는 인터페이스입니다.
 */
public interface NoticeEvent {

	String getTitle();

	String getFormat();

	String getDeepLinkFormat();

	List<NoticeAttribute> getAttributes();

	NoticeType getNoticeType();

	List<String> getDeepLinkParameters();

	default List<String> getAttributesAsString() {
		return getAttributes().stream().map(Enum::name).collect(Collectors.toList());
	}
}
