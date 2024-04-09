package org.johoeunsae.exchangediary.diary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.dto.*;
import org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus;
import org.springframework.data.domain.Pageable;
import org.johoeunsae.exchangediary.member.service.MemberQueryService;
import org.johoeunsae.exchangediary.note.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class DiaryFacadeServiceImpl implements DiaryFacadeService {

	private final DiaryQueryService diaryQueryService;
	private final DiaryService diaryService;
	private final NoteService noteService;
	private final MemberQueryService memberQueryService;

	/*-----------------------------------------READ-----------------------------------------*/
	@Override
	@Transactional(readOnly = true)
	public InvitationCodeDto getDiaryInvitationCode(Long loginMemberId, Long diaryId) {
		log.info("Called getDiaryInvitationCode loginMemberId: {}, diaryId: {}", loginMemberId,
				diaryId);
		diaryService.validateInvitationCodeGenerate(loginMemberId, diaryId);
		return diaryService.generateDiaryInvitationCode(loginMemberId, diaryId);
	}

	@Override
	public List<DiaryPreviewDto> getMyDiaries(Long loginMemberId) {
		return diaryQueryService.getMyDiaries(loginMemberId);
	}

	@Override
	public DiaryPreviewDto getMyDiary(Long loginMemberId, Long diaryId) {
		return diaryQueryService.getMyDiary(loginMemberId, diaryId);
	}

	@Override
	public List<DiaryRecentNoteDto> getMyDiariesNewNotes(Long loginMemberId) {
		List<DiaryRecentNoteDto> diaryRecentNoteDtos = diaryQueryService.getMyDiariesNewNotes(
				loginMemberId);
		return diaryRecentNoteDtos;
	}

	@Override
	public List<DiaryMemberPreviewDto> getMemberPreviewList(Long loginMemberId, Long diaryId) {
		return memberQueryService.getMemberPreviewListInDairy(loginMemberId, diaryId);
	}

	@Override
	public NotePreviewPaginationDto getNotePreviewPaginationFromDiary(Long loginUserId,
	         Long diaryId, Pageable pageable) {
		return diaryQueryService.getNotePreviewPaginationFromDiary(loginUserId, diaryId, pageable);
	}

	@Override
	public DiaryNoteViewDto getNoteFromDiary(Long loginMemberId, Long diaryId, Long noteId) {
		return diaryQueryService.getNoteFromDiary(loginMemberId, diaryId, noteId);
	}

	@Override
	@Transactional(readOnly = true)
	public DiaryPreviewDto getDiaryWithInvitationCode(String code) {
		log.info("Called getDiaryWithInvitationCode code: {}", code);
		Long diaryId = diaryService.getDiaryIdByInvitationCode(code);
		return diaryQueryService.getDiaryInfo(diaryId);
	}

	/*-----------------------------------------CUD-----------------------------------------*/

	@Override
	@Transactional
	public DiaryPreviewDto createDiary(Long loginMemberId, DiaryCreateRequestDto dto) {
		log.info("Called createDiary loginMemberId: {}, dto: {}", loginMemberId, dto);

		switch (dto.getCoverType()) {
			case IMAGE:
				return diaryService.createImageCoverDiary(loginMemberId, dto);
			case COLOR:
				return diaryService.createColorCoverDiary(loginMemberId, dto);
			default:
				throw CommonExceptionStatus.INCORRECT_ARGUMENT.toServiceException();
		}
	}

	@Override
	@Transactional
	public void deleteDiary(Long loginMemberId, Long diaryId) {
		log.info("Called deleteDiary loginMemberId: {}, diaryId: {}", loginMemberId, diaryId);
		diaryService.deleteDiary(diaryId, loginMemberId);
		noteService.tearOffNotesFromDiary(diaryId);
	}

	@Override
	@Transactional
	public DiaryPreviewDto editDiary(Long loginMemberId, Long diaryId, DiaryUpdateRequestDto dto) {
		log.info("Called editDiary loginMemberId: {}, diaryId: {}, dto: {}", loginMemberId,
				diaryId, dto);
		return diaryService.editDiary(loginMemberId, diaryId, dto);
	}

	@Override
	@Transactional
	public DiaryPreviewDto joinDiaryWithInvitation(Long loginMemberId, Long diaryId, String invitationCode) {
		log.info("Called joinDiaryWithInvitation loginMemberId: {}, diaryId: {}, invitationCode: {}",
				loginMemberId, diaryId, invitationCode);
		Long invitationCodeDiaryId = diaryService.getDiaryIdByInvitationCode(invitationCode);
		if (!diaryId.equals(invitationCodeDiaryId)) {
			throw DiaryExceptionStatus.INVALID_INVITATION_CODE.toServiceException();
		}
		diaryService.addMemberToDiary(loginMemberId, diaryId);
		return diaryQueryService.getDiaryInfo(diaryId);
	}

	@Override
	@Transactional
	public void changeDiaryMaster(Long loginMemberId, Long diaryId, Long targetMemberId) {
		log.info("Called changeDiaryMaster loginMemberId: {}, diaryId: {}, targetMemberId: {}",
				loginMemberId,
				diaryId, targetMemberId);
		diaryService.changeDiaryMaster(loginMemberId, diaryId, targetMemberId);
	}

	@Override
	@Transactional
	public void leaveDiary(Long loginMemberId, Long diaryId) {
		log.info("Called leaveDiary loginMemberId: {}, diaryId: {}", loginMemberId, diaryId);
		diaryService.leaveDiary(loginMemberId, diaryId);
	}

	@Override
	@Transactional
	public void kickDiaryMember(Long loginMemberId, Long diaryId, Long memberId) {
		log.info("Called kickDiaryMember loginMemberId: {}, diaryId: {}, memberId: {}",
				loginMemberId, diaryId, memberId);
		diaryService.kickDiaryMember(loginMemberId, diaryId, memberId);
	}

	@Override
	@Transactional
	public NotePreviewDto createNoteToDiary(Long loginMemberId, Long diaryId, NoteCreateRequestDto dto) {
		log.info("Called createNoteToDiary loginMemberId: {}, diaryId: {}, dto: {}", loginMemberId,
				diaryId, dto);
		return diaryService.createNoteToDiary(loginMemberId, diaryId, dto);
	}

	@Override
	@Transactional
	public void tearOffNoteFromDiary(Long loginMemberId, Long diaryId, Long noteId) {
		log.info("Called tearOffNoteFromDiary loginMemberId: {}, diaryId: {}, noteId: {}",
				loginMemberId, diaryId, noteId);
		noteService.tearOffNoteFromDiary(loginMemberId, diaryId, noteId);
	}
}
