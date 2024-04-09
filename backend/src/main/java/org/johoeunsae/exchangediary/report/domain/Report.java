package org.johoeunsae.exchangediary.report.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.blacklist.domain.Blacklist;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REPORT") @Entity
public class Report extends IdentityIdDomain {

	/* 고유 정보 { */
	@Enumerated(EnumType.STRING)
	@Column(name = "REPORT_TYPE", nullable = false, length = 20)
	private ReportType reportType;
	@Column(name = "REASON")
	private String reason;
	@Column(name = "CREATE_AT", nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "REPORT_MEMBER_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member from;
	@JoinColumn(name = "REPORTED_MEMBER_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member to;
	@JoinColumn(name = "BLACKLIST_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Blacklist blacklist;
	@JoinColumn(name = "NOTE_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Note note;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Report(Member from, Member to, LocalDateTime createdAt, ReportType reportType,
			String reason, Note note) {
		this.reportType = reportType;
		this.reason = reason;
		this.createdAt = createdAt;
		this.from = from;
		this.to = to;
		this.note = note;
	}

	public static Report of(MemberFromTo fromTo, LocalDateTime now, ReportType reportType,
			String reason, Note note) {
		RuntimeExceptionThrower.checkIdLoaded(fromTo.getFrom());
		RuntimeExceptionThrower.checkIdLoaded(fromTo.getTo());
		return new Report(fromTo.getFrom(), fromTo.getTo(), now, reportType, reason, note);
	}
	/* } 생성자 */

	/* 편의 메서드 { */
	public void setBlacklist(Blacklist blacklist) {
		RuntimeExceptionThrower.checkIdLoaded(blacklist);
		this.blacklist = blacklist;
	}

	public void setNoteId(Note note) {
		RuntimeExceptionThrower.checkIdLoaded(note);
		this.note = note;
	}
	/* } 편의 메서드 */

}
