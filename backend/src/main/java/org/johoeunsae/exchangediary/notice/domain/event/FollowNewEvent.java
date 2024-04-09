package org.johoeunsae.exchangediary.notice.domain.event;

import static org.johoeunsae.exchangediary.notice.domain.NoticeAttribute.INNER_APP_NOTICE;
import static org.johoeunsae.exchangediary.notice.domain.NoticeAttribute.PUSH_NOTICE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.johoeunsae.exchangediary.notice.domain.NoticeAttribute;
import org.johoeunsae.exchangediary.notice.domain.NoticeParameter;
import org.johoeunsae.exchangediary.notice.domain.NoticeParameter.NoticeParameterType;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

@Builder
@Getter
@ToString
@FieldNameConstants
public class FollowNewEvent implements NoticeEvent {
	@Builder.Default
	private final String title = "새 팔로우";
	@Builder.Default
	private final String format = "%s님이 팔로우하기 시작했습니다!";
	@Builder.Default
	private final String deepLinkFormat = NoticeParameter.getDeepLinkFormat() + "님이 팔로우하기 시작했습니다!";
	@Builder.Default
	private final NoticeType noticeType = NoticeType.FOLLOW_CREATE_FROM;
	@Builder.Default
	private final List<NoticeAttribute> attributes = List.of(PUSH_NOTICE, INNER_APP_NOTICE);

	private final Long fromId;
	private final String fromName;
	private final Long receiverId;
	private final LocalDateTime createdAt;

	@Override
	public List<String> getDeepLinkParameters() {
		return List.of(
				new NoticeParameter(NoticeParameterType.MEMBER, fromName, fromId).getEncodedString()
		);
	}

	@Override
	public List<String> getAttributesAsString() {
		return attributes.stream().map(Enum::name).collect(Collectors.toList());
	}

}
