package org.johoeunsae.exchangediary.note.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTE_READ") @Entity
public class NoteRead extends IdDomain<NoteMemberCompositeKey> implements Validatable {

	/* 고유 정보 { */
	@ToString.Exclude
	@EmbeddedId
	private NoteMemberCompositeKey id;
	@Column(name = "READ_AT", nullable = false)
	private LocalDateTime readAt;
	@Column(name = "COUNTS", nullable = false)
	private Integer counts;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member member;
	@JoinColumn(name = "NOTE_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Note note;
	/* } 연관 정보 */

	/* 생성자 { */
	protected NoteRead(Member member, Note note, LocalDateTime readAt, int counts) {
		this.id = NoteMemberCompositeKey.of(member.getId(), note.getId());
		this.readAt = readAt;
		this.counts = counts;
		this.member = member;
		this.note = note;
		RuntimeExceptionThrower.validateDomain(this);
	}

	public static NoteRead of(Member member, Note note, LocalDateTime now, int counts) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		RuntimeExceptionThrower.checkIdLoaded(note);
		return new NoteRead(member, note, now, counts);
	}
	/* } 생성자 */

	@Override public boolean isValid() {
		return this.id.isValid()
				&& this.readAt != null
				&& this.counts != null;
	}
}
