package org.johoeunsae.exchangediary.member.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_TOKEN")
@Entity
public class MemberToken extends IdentityIdDomain {

	/* 고유 정보 { */
	private String token;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "MEMBER_ID", nullable = false)
	@OneToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@MapsId
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	protected MemberToken(
			Member member,
			String token,
			LocalDateTime createdAt) {
		this.member = member;
		this.token = token;
		this.createdAt = createdAt;
	}

	public static MemberToken of(
			Member member,
			String token,
			LocalDateTime createdAt) {
		return new MemberToken(member, token, createdAt);
	}
	/* } 생성자 */
}
