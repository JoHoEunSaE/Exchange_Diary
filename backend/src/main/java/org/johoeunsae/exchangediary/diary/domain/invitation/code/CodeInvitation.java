package org.johoeunsae.exchangediary.diary.domain.invitation.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.johoeunsae.exchangediary.diary.domain.invitation.Invitation;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus;

import java.time.Duration;
import java.time.LocalDateTime;

@ToString
@Log4j2
@EqualsAndHashCode
public class CodeInvitation implements Invitation {

	private static final int DEFAULT_CODE_LENGTH = 6;
	private static final int DEFAULT_DURATION_MINUTES = 10;
	public static final Duration DEFAULT_DURATION = Duration.ofMinutes(DEFAULT_DURATION_MINUTES);

	private final Long diaryId;
	private final String code;
	private final LocalDateTime createdAt;
	private final LocalDateTime expiredAt;
	private final Duration duration;

	private CodeInvitation(Long diaryId) {
		if (diaryId == null) {
			throw new DomainException(CommonExceptionStatus.INCORRECT_ARGUMENT);
		}
		this.diaryId = diaryId;
		this.createdAt = LocalDateTime.now();
		this.code = RandomStringUtils.randomAlphanumeric(DEFAULT_CODE_LENGTH);
		this.duration = DEFAULT_DURATION;
		this.expiredAt = createdAt.plus(duration);
	}

	private CodeInvitation(Long diaryId, LocalDateTime createdAt, LocalDateTime expiredAt, String value) {
		this.diaryId = diaryId;
		this.createdAt = createdAt;
		this.expiredAt = expiredAt;
		this.code = value;
		this.duration = Duration.between(createdAt, expiredAt);
	}

	public static CodeInvitation of(Long diaryId) {
		return new CodeInvitation(diaryId);
	}

	@JsonCreator
	public CodeInvitation CodeInvitation(
			@JsonProperty("diaryId") long diaryId,
			@JsonProperty("createdAt") LocalDateTime createdAt,
			@JsonProperty("expiredAt") LocalDateTime expiredAt,
			@JsonProperty("value") String value) {
		return new CodeInvitation(diaryId, createdAt, expiredAt, value);
	}

	@Override
	public Long getDiaryId() {
		return diaryId;
	}

	@Override
	public String getValue() {
		return code;
	}

	@Override
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public LocalDateTime getExpiredAt() {
		return expiredAt;
	}

	@Override
	public boolean isExpired(LocalDateTime now) {
		return now.isAfter(expiredAt);
	}

	public Duration getRemainDuration(LocalDateTime now) {
		return Duration.between(now, expiredAt);
	}
}
