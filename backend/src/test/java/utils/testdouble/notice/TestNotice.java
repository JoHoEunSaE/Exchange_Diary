package utils.testdouble.notice;


import lombok.Builder;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.notice.domain.Notice;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;
import utils.testdouble.TestEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Builder
public class TestNotice implements TestEntity<Notice, Long> {

	public static final String DEFAULT_TITLE = "title";
	public static final String DEFAULT_CONTENT = "content";
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final NoticeType DEFAULT_NOTICE_TYPE = NoticeType.ANNOUNCEMENT;

	@Builder.Default
	private String title = DEFAULT_TITLE;
	@Builder.Default
	private String content = DEFAULT_CONTENT;
	@Builder.Default
	private Member member = null;
	@Builder.Default
	private NoticeType noticeType = DEFAULT_NOTICE_TYPE;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;

	@Override public Notice asEntity() {
		return Notice.of(
				this.member,
				this.title,
				this.content,
				this.noticeType,
				this.createdAt
		);
	}

	@Override public Notice asMockEntity(Long aLong) {
		Notice notice = mock(Notice.class);
		lenient().when(notice.getTitle()).thenReturn(this.title);
		lenient().when(notice.getContent()).thenReturn(this.content);
		lenient().when(notice.getReceiver()).thenReturn(this.member);
		lenient().when(notice.getNoticeType()).thenReturn(this.noticeType);
		lenient().when(notice.getCreatedAt()).thenReturn(this.createdAt);
		return notice;
	}
}
