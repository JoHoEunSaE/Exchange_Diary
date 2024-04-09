package org.johoeunsae.exchangediary.report.service;

import org.johoeunsae.exchangediary.dto.ReportRequestDto;

public interface ReportService {

	/**
	 * 회원 신고
	 * @param loginMemberId 신고자
	 * @param targetMemberId 신고당한 회원
	 * @param reportRequestDto 신고 내용
	 */
	void reportMember(Long loginMemberId, Long targetMemberId, ReportRequestDto reportRequestDto);

	/**
	 * 노트 신고
	 * @param loginMemberId 신고자
	 * @param noteId 	  신고당한 노트
	 * @param reportRequestDto 신고 내용
	 * @return 신고당한 노트의 작성자 ID
	 */
	void reportNote(Long loginMemberId, Long noteId, ReportRequestDto reportRequestDto);

}
