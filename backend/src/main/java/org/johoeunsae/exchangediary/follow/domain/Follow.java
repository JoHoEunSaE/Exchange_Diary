package org.johoeunsae.exchangediary.follow.domain;

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

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FOLLOW") @Entity
public class Follow extends IdDomain<MemberCompositeKey> {

	/* 고유 정보 { */
	@ToString.Exclude
	@EmbeddedId
	private MemberCompositeKey id;
	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
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
	protected Follow(Member from, Member to, LocalDateTime createdAt) {
		this.id = MemberCompositeKey.of(from.getId(), to.getId());
		this.createdAt = createdAt;
		this.from = from;
		this.to = to;
	}

	public static Follow of(MemberFromTo fromTo, LocalDateTime now) {
		RuntimeExceptionThrower.checkIdLoaded(fromTo.getFrom());
		RuntimeExceptionThrower.checkIdLoaded(fromTo.getTo());
		return new Follow(fromTo.getFrom(), fromTo.getTo(), now);
	}
	/* } 생성자 */
}
