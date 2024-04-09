package org.johoeunsae.exchangediary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;

@NoArgsConstructor
@Getter
public class NoteRelatedInfoDto {
	private Note note;
	private Member member;
	private DiarySimpleInfoDto diaryInfo;
	private boolean hasRead;
	private boolean isBlocked;

	@Builder
	public NoteRelatedInfoDto(Note note, Member member, Long diaryId,
			String title, String groupName, boolean hasRead, boolean isBlocked) {
		this.note = note;
		this.diaryInfo = DiarySimpleInfoDto.builder()
				.id(diaryId)
				.title(title)
				.groupName(groupName)
				.build();
		this.member = member;
		this.hasRead = hasRead;
		this.isBlocked = isBlocked;
	}
}
