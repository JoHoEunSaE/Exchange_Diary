package org.johoeunsae.exchangediary.diary.domain.invitation;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 해당 인터페이스를 구현하는 클래스는 Redis 등의 저장소로부터 역직렬화하여 객체를 만들 수 있도록 기본 생성자를 정의해야 합니다.
 */
public interface Invitation {

	Long getDiaryId();

	String getValue();

	LocalDateTime getCreatedAt();

	LocalDateTime getExpiredAt();

	Duration getRemainDuration(LocalDateTime now);

	boolean isExpired(LocalDateTime now);
}
