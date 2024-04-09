package org.johoeunsae.exchangediary.ping.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.notice.domain.NoticeType;

@Getter
@AllArgsConstructor
@ToString
public class AlarmTestDto {

	private Long fromId;
	private String fromName;
	private Long toId;
	private String title;
	private String content;
	private NoticeType noticeType;
}
