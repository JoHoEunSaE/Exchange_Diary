package org.johoeunsae.exchangediary.notice.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;


/**
 * FCM 푸시 알림을 위한 디바이스 토큰 - 기기의 식별 토큰입니다.
 * <br>
 * 프론트엔드에서 전달하는 방식으로 생성, 관리합니다.
 * <p>
 * 한 유저가 여러 기기(디바이스 토큰)을 가질 수 있습니다.
 */
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DEVICE_REGISTRY")
@Entity
public class DeviceRegistry extends IdentityIdDomain implements Validatable {

	public static final int MAX_DEVICE_COUNT = 5;

	/* 고유 정보 { */
	@Column(name = "TOKEN", nullable = false)
	private String token;
	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "MEMBER_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	private DeviceRegistry(Member member, String token, LocalDateTime createdAt) {
		this.member = member;
		this.token = token;
		this.createdAt = createdAt;
	}

	public static DeviceRegistry of(Member member, String deviceToken, LocalDateTime createdAt) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		return new DeviceRegistry(member, deviceToken, createdAt);
	}
	/* } 생성자 */

	@Override
	public boolean isValid() {
		return member != null && token != null && !token.isEmpty() && createdAt != null;
	}
}
