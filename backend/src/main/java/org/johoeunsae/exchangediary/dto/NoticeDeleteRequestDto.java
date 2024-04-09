package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(name = "NoticeDeleteRequestDto", description = "알림 삭제 요청 Dto")
@Getter
@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
public class NoticeDeleteRequestDto {
	@Schema(name = "noticeIds", description = "알림 아이디 배열")
	private List<Long> noticeIds;
}
