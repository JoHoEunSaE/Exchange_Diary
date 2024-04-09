package org.johoeunsae.exchangediary.bookmark.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.utils.domain.IdDomain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BOOKMARK") @Entity
public class Bookmark extends IdDomain<NoteMemberCompositeKey> {

	/* 고유 정보 { */
	@ToString.Exclude
	@EmbeddedId
	private NoteMemberCompositeKey id;
	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member member;
	@JoinColumn(name = "NOTE_ID", insertable = false, updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Note note;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Bookmark(Member member, Note note, LocalDateTime createdAt) {
		this.id = NoteMemberCompositeKey.of(member.getId(), note.getId());
		this.createdAt = createdAt;
		this.member = member;
		this.note = note;
	}

	public static Bookmark of(Member member, Note note, LocalDateTime now) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		RuntimeExceptionThrower.checkIdLoaded(note);
		return new Bookmark(member, note, now);
	}
	/* } 생성자 */

	public boolean isNoteId(Long noteId) {
		if (this.id == null || this.isNew())
			throw new DomainException(CommonExceptionStatus.NOT_PERSISTED);
		return this.getId().getNoteId().equals(noteId);
	}

	public boolean isMemberId(Long memberId) {
		if (this.id == null || this.isNew())
			throw new DomainException(CommonExceptionStatus.NOT_PERSISTED);
		return this.getId().getMemberId().equals(memberId);
	}
}
