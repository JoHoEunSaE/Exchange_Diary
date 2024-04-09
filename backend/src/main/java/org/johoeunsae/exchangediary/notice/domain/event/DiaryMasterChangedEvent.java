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
public class DiaryMasterChangedEvent implements NoticeEvent {
	@Builder.Default
	private final String title = "일기장 방장 변경";
	@Builder.Default
	private final String format = "%s님이 %s의 방장이 되었습니다.";
	@Builder.Default
	private final String deepLinkFormat = NoticeParameter.getDeepLinkFormat() + "님이 " + NoticeParameter.getDeepLinkFormat() + "의 방장이 되었습니다.";
	@Builder.Default
	private final NoticeType noticeType = NoticeType.DIARY_MASTER_CHANGED_TO;
	@Builder.Default
	private final List<NoticeAttribute> attributes = List.of(NoticeAttribute.PUSH_NOTICE, NoticeAttribute.INNER_APP_NOTICE);

	private final Long changedMasterId;
	private final String changedMasterName;
	private final Long diaryId;
	private final String diaryTitle;
	private final Long receiverId;
	private final LocalDateTime createdAt;

	@Override public List<String> getDeepLinkParameters() {
		return List.of(
				new NoticeParameter(NoticeParameterType.MEMBER, changedMasterName, changedMasterId).getEncodedString(),
				new NoticeParameter(NoticeParameterType.DIARY, diaryTitle, diaryId).getEncodedString()
		);
	}
}
