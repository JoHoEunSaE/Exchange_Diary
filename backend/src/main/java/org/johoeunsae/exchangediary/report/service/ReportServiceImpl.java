package org.johoeunsae.exchangediary.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.dto.ReportRequestDto;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.ReportExceptionStatus;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.johoeunsae.exchangediary.report.domain.Report;
import org.johoeunsae.exchangediary.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

	private static final int NOTE_BLIND_THRESHOLD = 5;
	private final MemberRepository memberRepository;
	private final NoteRepository noteRepository;
	private final ReportRepository reportRepository;

	@Override
	public void reportMember(Long loginMemberId, Long targetMemberId, ReportRequestDto reportRequestDto) {
		log.info("[회원 신고] targetMemberId : {}, 사유 : {}", targetMemberId, reportRequestDto.getReportType());
		Member loginMember = memberRepository.findById(loginMemberId)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);
		Member target = memberRepository.findById(targetMemberId)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);

		// 중복 신고 검사
		reportRepository.findByMemberFromIdAndMemberToId(loginMemberId, targetMemberId)
				.ifPresent(r -> {
					throw ReportExceptionStatus.DUPLICATE_REPORT.toServiceException();
				});

		// 본인일 경우 신고 불가
		if (loginMemberId.equals(targetMemberId)) {
			throw ReportExceptionStatus.CANNOT_REPORT_MYSELF.toServiceException();
		}

		Report newReport = Report.of(MemberFromTo.of(loginMember, target), LocalDateTime.now(),
				reportRequestDto.getReportType(), reportRequestDto.getReason(), null);
		reportRepository.save(newReport);
	}

	@Override
	public void reportNote(Long loginMemberId, Long noteId, ReportRequestDto reportRequestDto) {
		log.info("[일기장 신고] targetNoteId : {}, 사유 : {}", noteId, reportRequestDto.getReportType());
		Member loginMember = memberRepository.findById(loginMemberId)
				.orElseThrow(MemberExceptionStatus.NOT_FOUND_MEMBER::toServiceException);
		Note note = noteRepository.findById(noteId)
				.orElseThrow(NoteExceptionStatus.NOT_FOUND_NOTE::toServiceException);
		Member target = note.getMember();

		// 중복 신고 검사
		reportRepository.findByMemberFromIdAndMemberToId(loginMemberId, target.getId())
				.ifPresent(r -> {
					throw ReportExceptionStatus.DUPLICATE_REPORT.toServiceException();
				});

		// 본인일 경우 신고 불가
		if (loginMemberId.equals(target.getId())) {
			throw ReportExceptionStatus.CANNOT_REPORT_MYSELF.toServiceException();
		}

		Report newReport = Report.of(MemberFromTo.of(loginMember, note.getMember()), LocalDateTime.now(),
				reportRequestDto.getReportType(), reportRequestDto.getReason(), note);
		reportRepository.save(newReport);

		// 일정 횟수 이상 신고 시 블라인드 처리
		if (reportRepository.countByNoteId(noteId) >= NOTE_BLIND_THRESHOLD) {
			if (note.getVisibleScope() == VisibleScope.PRIVATE) {
				note.updateVisibleScope(VisibleScope.PRIVATE_BLIND);
			} else {
				note.updateVisibleScope(VisibleScope.PUBLIC_BLIND);
			}
			noteRepository.save(note);
		}
	}
}
