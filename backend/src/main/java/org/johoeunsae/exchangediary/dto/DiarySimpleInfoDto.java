package org.johoeunsae.exchangediary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DiarySimpleInfoDto {
	private Long id;
	private String title;
	private String groupName;

	@Builder
	public DiarySimpleInfoDto(Long id, String title, String groupName) {
		this.id = id;
		this.title = title;
		this.groupName = groupName;
	}
}
