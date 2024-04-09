package org.johoeunsae.exchangediary.blacklist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BLACKLIST") @Entity
public class Blacklist extends IdentityIdDomain implements Validatable {

	/* 고유 정보 { */
	@Column(name = "STARTED_AT", nullable = false)
	private LocalDateTime startedAt;
	@Column(name = "ENDED_AT", nullable = false)
	private LocalDateTime endedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "MEMBER_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Blacklist(Member member, LocalDateTime startedAt, LocalDateTime endedAt) {
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.member = member;
	}

	public static Blacklist of(Member member, LocalDateTime now, LocalDateTime endedAt) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		return new Blacklist(member, now, endedAt);
	}

	public static Blacklist of(Member member, LocalDateTime now, int days) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		return new Blacklist(member, now, now.plusDays(days));
	}

	/* } 생성자 */

	@Override
	public boolean isValid() {
		if (startedAt == null || endedAt == null || member == null) {
			return false;
		}
		if (startedAt.isAfter(endedAt)) {
			return false;
		}
		return true;
	}
}
