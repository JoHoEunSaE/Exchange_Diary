package org.johoeunsae.exchangediary.notice.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.notice.domain.NoticeAttribute;
import org.johoeunsae.exchangediary.notice.domain.NoticeParameter;
import org.johoeunsae.exchangediary.notice.domain.NoticeParameter.NoticeParameterType;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

@Builder
@Getter
public class DiaryMemberKickEvent implements NoticeEvent {

	@Builder.Default
	private final String title = "일기장 추방";
	@Builder.Default
	private final String format = "%s 일기장에서 추방되었습니다.";
	@Builder.Default
	private final String deepLinkFormat = NoticeParameter.getDeepLinkFormat() + " 일기장에서 추방되었습니다.";
	@Builder.Default
	private final NoticeType noticeType = NoticeType.DIARY_MEMBER_KICK_FROM;
	@Builder.Default
	private final List<NoticeAttribute> attributes = List.of(NoticeAttribute.PUSH_NOTICE, NoticeAttribute.INNER_APP_NOTICE);

	private final Long diaryId;
	private final String diaryTitle;
	private final Long receiverId;
	private final LocalDateTime createdAt;

	@Override public List<String> getDeepLinkParameters() {
		return List.of(
				new NoticeParameter(NoticeParameterType.DIARY, diaryTitle, diaryId).getEncodedString()
		);
	}
}
