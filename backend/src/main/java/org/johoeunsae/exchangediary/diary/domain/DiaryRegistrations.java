package org.johoeunsae.exchangediary.diary.domain;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;

import java.util.List;
import java.util.Objects;

/**
 * Registration에 대한 일급 컬렉션 객체입니다.
 */
@RequiredArgsConstructor
public class DiaryRegistrations {

	private final List<Registration> registrations;

	public boolean hasMember(Member member) {
		return registrations.stream()
				.anyMatch(r -> r.getMember().equals(member));
	}

	public boolean hasMember(Long memberId) {
		return registrations.stream()
				.anyMatch(r -> Objects.requireNonNull(r.getMember().getId()).equals(memberId));
	}

	public boolean hasDiaryBy(Diary diary) {
		return registrations.stream()
				.anyMatch(r -> r.getDiary().equals(diary));
	}

	public boolean hasDiaryBy(Long diaryId) {
		return registrations.stream()
				.anyMatch(r -> Objects.requireNonNull(r.getDiary().getId()).equals(diaryId));
	}

	public boolean hasDiaryOf(Note note) {
		return registrations.stream()
				.anyMatch(r -> Objects.requireNonNull(r.getDiary().getId()).equals(note.getDiaryId()));
	}


}
