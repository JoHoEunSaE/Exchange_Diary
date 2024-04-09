package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

import java.time.LocalDateTime;

@Schema(description = "알림 정보")
@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class NoticeDto {
	@Schema(description = "알림의 ID", example = "1")
	private final Long id;
	@Schema(description = "알림의 종류", example = "ANNOUNCEMENT, DIARY_NOTE_FROM_TO, FOLLOW_CREATE_FROM...")
	private final NoticeType noticeType;
	@Schema(description = "알림의 제목", example = "새로운 반응이 달렸습니다.")
	private final String title;
	@Schema(description = "알림의 내용", example = "johoeunsae님이 당신의 글에 좋아요를 눌렀습니다.")
	private final String content;
	@Schema(description = "알림이 생성된 시간", example = "2021-08-01T00:00:00")
	private final LocalDateTime createdAt;
}
