package org.johoeunsae.exchangediary.diary.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.keys.DiaryMemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdDomain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REGISTRATION") @Entity
public class Registration extends IdDomain<DiaryMemberCompositeKey> {

	/* 고유 정보 { */
	@EmbeddedId
	@ToString.Exclude
	private DiaryMemberCompositeKey id;
	@Column(name = "REGISTERED_AT", nullable = false)
	private LocalDateTime registeredAt;
	/* } 고유 정보 */

	/* 연관 관계 { */
	@JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member member;
	@JoinColumn(name = "DIARY_ID", insertable = false, updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Diary diary;
	/* } 연관 관계 */

	/* 생성자 { */
	protected Registration(Member member, Diary diary, LocalDateTime registeredAt) {
		this.id = DiaryMemberCompositeKey.of(diary.getId(), member.getId());
		this.registeredAt = registeredAt;
		this.member = member;
		this.diary = diary;
	}

	public static Registration of(Member member, Diary diary, LocalDateTime now) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		RuntimeExceptionThrower.checkIdLoaded(diary);
		return new Registration(member, diary, now);
	}
	/* } 생성자 */
}
