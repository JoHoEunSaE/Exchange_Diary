package org.johoeunsae.exchangediary.keys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

@Getter @ToString @EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class NoteMemberCompositeKey implements Serializable, Validatable {

	@Column(name = "NOTE_ID", nullable = false)
	private Long noteId;

	@Column(name = "MEMBER_ID", nullable = false)
	private Long memberId;

	protected NoteMemberCompositeKey(Long memberId, Long noteId) {
		this.memberId = memberId;
		this.noteId = noteId;
	}

	public static NoteMemberCompositeKey of(Long memberId, Long noteId) {
		return new NoteMemberCompositeKey(memberId, noteId);
	}

	@Override
	public boolean isValid() {
		return memberId != null && noteId != null;
	}
}

