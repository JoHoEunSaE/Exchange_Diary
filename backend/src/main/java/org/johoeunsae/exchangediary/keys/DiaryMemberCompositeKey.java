package org.johoeunsae.exchangediary.keys;

import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

@Getter @ToString @EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class DiaryMemberCompositeKey implements Serializable, Validatable {

	@Column(name = "MEMBER_ID", nullable = false)
	private Long memberId;

	@Column(name = "DIARY_ID", nullable = false)
	private Long diaryId;

	protected DiaryMemberCompositeKey(Long diaryId, Long memberId) {
		this.diaryId = diaryId;
		this.memberId = memberId;
	}

	public static DiaryMemberCompositeKey of(Long diaryId, Long memberId) {
		return new DiaryMemberCompositeKey(diaryId, memberId);
	}

	@Override
	public boolean isValid() {
		return memberId != null && diaryId != null;
	}
}
