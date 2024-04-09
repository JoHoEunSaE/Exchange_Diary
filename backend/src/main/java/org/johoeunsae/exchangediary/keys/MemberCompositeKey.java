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
public class MemberCompositeKey implements Serializable, Validatable {
	@Column(name = "MEMBER_ID", nullable = false)
	private Long memberId;
	@Column(name = "TARGET_MEMBER_ID", nullable = false)
	private Long targetMemberId;

	protected MemberCompositeKey(Long memberId, Long targetMemberId) {
		this.memberId = memberId;
		this.targetMemberId = targetMemberId;
	}

	public static MemberCompositeKey of(Long memberId, Long targetMemberId) {
		return new MemberCompositeKey(memberId, targetMemberId);
	}

	@Override
	public boolean isValid() {
		return memberId != null && targetMemberId != null;
	}
}
