package org.johoeunsae.exchangediary.like.domain;

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
@Table(name = "LIKE") @Entity
public class Like extends IdDomain<NoteMemberCompositeKey> {

	/* 고유 정보 { */
	@ToString.Exclude
	@EmbeddedId
	private NoteMemberCompositeKey id;
	@Column(name = "LIKED_AT", nullable = false)
	private LocalDateTime likedAt;
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
	protected Like(Member member, Note note, LocalDateTime likedAt) {
		this.id = NoteMemberCompositeKey.of(member.getId(), note.getId());
		this.likedAt = likedAt;
		this.member = member;
		this.note = note;
	}

	public static Like of(Member member, Note note, LocalDateTime now) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		RuntimeExceptionThrower.checkIdLoaded(note);
		return new Like(member, note, now);
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

	public Long getMemberId() {
		if (this.id == null) {
			throw new DomainException(CommonExceptionStatus.NOT_PERSISTED);
		}
		return this.getId().getMemberId();
	}
}
