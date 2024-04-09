package org.johoeunsae.exchangediary.notice.domain.event;


import static org.johoeunsae.exchangediary.notice.domain.NoticeAttribute.INNER_APP_NOTICE;
import static org.johoeunsae.exchangediary.notice.domain.NoticeAttribute.PUSH_NOTICE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.notice.domain.NoticeAttribute;
import org.johoeunsae.exchangediary.notice.domain.NoticeParameter;
import org.johoeunsae.exchangediary.notice.domain.NoticeParameter.NoticeParameterType;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

@Builder
@Getter
public class DiaryNewMemberEvent implements NoticeEvent {
	@Builder.Default
	private final String title = "새 일기장 멤버";
	@Builder.Default
	private final String format = "%s님이 %s 일기장에 참여하셨습니다!";
	@Builder.Default
	private final String deepLinkFormat = NoticeParameter.getDeepLinkFormat() + " 님이 " + NoticeParameter.getDeepLinkFormat() + "일기장에 참여하셨습니다!";
	@Builder.Default
	private final NoticeType noticeType = NoticeType.DIARY_MEMBER_FROM_TO;
	@Builder.Default
	private final List<NoticeAttribute> attributes = List.of(PUSH_NOTICE, INNER_APP_NOTICE);

	private final Long diaryId;
	private final String diaryTitle;
	private final String newMemberName;
	private final Long newMemberId;
	private final LocalDateTime createdAt;

	@Override
	public List<String> getDeepLinkParameters() {
		return List.of(
			new NoticeParameter(NoticeParameterType.MEMBER, newMemberName, newMemberId).getEncodedString(),
			new NoticeParameter(NoticeParameterType.DIARY, diaryTitle, diaryId).getEncodedString()
		);
	}

	@Override
	public List<String> getAttributesAsString() {
		return attributes.stream().map(Enum::name).collect(Collectors.toList());
	}
}
