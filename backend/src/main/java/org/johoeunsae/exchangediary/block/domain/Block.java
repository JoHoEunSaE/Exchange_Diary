package org.johoeunsae.exchangediary.block.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BLOCK") @Entity
public class Block extends IdDomain<MemberCompositeKey> implements Validatable {

	/* 고유 정보 { */
	@ToString.Exclude
	@EmbeddedId
	private MemberCompositeKey id;
	@Column(name = "BLOCKED_AT", nullable = false)
	private LocalDateTime blockedAt;
	/* } 고유 정보 */

	/* 연관 관계 { */
	@JoinColumn(name = "MEMBER_ID", updatable = false, insertable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member from;
	@JoinColumn(name = "TARGET_MEMBER_ID", updatable = false, insertable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member to;
	/* } 연관 관계 */

	/* 생성자 { */
	protected Block(Member fromMember, Member toMember, LocalDateTime blockedAt) {
		this.id = MemberCompositeKey.of(fromMember.getId(), toMember.getId());
		this.from = fromMember;
		this.to = toMember;
		this.blockedAt = blockedAt;
	}

	public static Block of(MemberFromTo fromTo, LocalDateTime now) {
		RuntimeExceptionThrower.checkIdLoaded(fromTo.getFrom());
		RuntimeExceptionThrower.checkIdLoaded(fromTo.getTo());
		return new Block(fromTo.getFrom(), fromTo.getTo(), now);
	}

	@Override
	public boolean isValid() {
		MemberCompositeKey id = getId();
		if (id == null || !id.isValid()) {
			return false;
		}
		if (getBlockedAt() == null) {
			return false;
		}
		return true;
	}
	/* } 생성자 */
}
