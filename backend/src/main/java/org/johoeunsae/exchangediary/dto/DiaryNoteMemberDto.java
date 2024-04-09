package org.johoeunsae.exchangediary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;

@NoArgsConstructor
@Getter
public class DiaryNoteMemberDto {
	private Long diaryId;
	private String diaryTitle;
	private String groupName;
	private Note note;
	private Member member;
	private boolean isBlocked;

	@Builder
	public DiaryNoteMemberDto(Long diaryId, String diaryTitle, String groupName,
			Note note, Member member, boolean isBlocked) {
		this.diaryId = diaryId;
		this.diaryTitle = diaryTitle;
		this.groupName = groupName;
		this.note = note;
		this.member = member;
		this.isBlocked = isBlocked;
	}
}