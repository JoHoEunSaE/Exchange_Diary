package org.johoeunsae.exchangediary.notice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTICE") @Entity
public class Notice extends IdentityIdDomain implements Validatable {

	@Column(nullable = false, name = "TITLE")
	private String title;

	@Column(nullable = false, name = "CONTENT")
	private String content;

	@JoinColumn(name = "RECEIVER_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member receiver;

	@Column(nullable = false, name = "NOTICE_TYPE")
	@Enumerated(EnumType.STRING)
	private NoticeType noticeType;

	@Column(name = "READ_AT")
	private LocalDateTime readAt;

	@Column(nullable = false, name = "CREATED_AT")
	private LocalDateTime createdAt;

	protected Notice(Member receiver, String title, String content, NoticeType noticeType, LocalDateTime createdAt) {
		this.title = title;
		this.content = content;
		this.receiver = receiver;
		this.noticeType = noticeType;
		this.createdAt = createdAt;
		RuntimeExceptionThrower.validateDomain(this);
	}

	public static Notice of(Member member, String title, String content, NoticeType noticeType, LocalDateTime createdAt) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		return new Notice(member, title, content, noticeType, createdAt);
	}

	public void read(LocalDateTime now) {
		this.readAt = now;
	}

	@Override
	public boolean isValid() {
		return title != null
				&& content != null
				&& receiver != null
				&& noticeType != null
				&& createdAt != null;
	}
}
