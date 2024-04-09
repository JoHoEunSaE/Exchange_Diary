package org.johoeunsae.exchangediary.note.service;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.block.service.BlockQueryService;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.diary.domain.DiaryRegistrations;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.dto.MemberNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.MyNotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NotePreviewPaginationDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.mapper.NoteMapper;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.repository.NoteReadRepository;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.johoeunsae.exchangediary.utils.QueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.stream.Collectors;

import static org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus.UNAUTHENTICATED;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;
import static org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus.NOT_FOUND_NOTE;

@QueryService
@RequiredArgsConstructor
public class NoteQueryServiceImpl implements NoteQueryService {

	private final NoteRepository noteRepository;
	private final NoteReadRepository noteReadRepository;
	private final MemberRepository memberRepository;
	private final RegistrationRepository registrationRepository;
	private final NoteMapper noteMapper;
	private final BookmarkRepository bookmarkRepository;
	private final LikeRepository likeRepository;
	private final BlockQueryService blockQueryService;

	/**
	 * 내가 쓴 일기 프리뷰 목록 조회
	 * <p>
	 * 최근에 작성된 일기부터 Preview로 조회합니다.
	 *
	 * @param loginUserId 로그인한 사용자의 ID
	 * @return
	 */
	@Override
	public MyNotePreviewPaginationDto getMyNotePreviewsByMemberId(Long loginUserId,
	                                                              Pageable pageable) {
		Page<Note> notes = noteRepository.findAllByMemberId(loginUserId, pageable);
		return noteMapper.toMyNotePreviewPaginationDto(notes.getTotalElements(),
				notes
						.stream()
						.map(noteMapper::toMyNotePreviewDto).collect(Collectors.toList()));
	}

	/**
	 * 현재 사용자(login user)의 입장에서 보는 target의 일기 프리뷰 목록 조회
	 *
	 * @param loginUserId 현재 사용자의 ID
	 * @param targetId    조회하고자 하는 사용자의 ID
	 * @return
	 */
	@Override
	public MemberNotePreviewPaginationDto getMemberNotePreviews(Long loginUserId, Long targetId,
	                                                            Pageable pageable) {
		Set<Long> noteReadIds = noteReadRepository.findAllByMemberId(loginUserId).stream()
				.map(noteRead -> noteRead.getId().getNoteId())
				.collect(Collectors.toSet());
		// TODO: 차단한 유저에 대한 처리 ?? exception?
		Page<Note> notes = noteRepository.findVisibleNotesByMemberId(loginUserId, targetId, pageable);
		return noteMapper.toMemberNotePreviewPaginationDto(
				notes.getTotalElements(), notes.stream().map(n ->
								noteMapper.toMemberNotePreviewDto(n, noteReadIds.contains(n.getId())))
						.collect(Collectors.toList()));
	}

	/**
	 * 일기 상세 조회
	 *
	 * @param loginUserId 로그인한 사용자의 ID
	 * @param noteId      조회하고자 하는 일기의 ID
	 * @return
	 */
	@Override
	public NoteViewDto getNoteView(Long loginUserId, Long noteId) {
		Note note = noteRepository.findVisibleNoteByNoteId(loginUserId, noteId).orElseThrow(NOT_FOUND_NOTE::toDomainException);
		Member member = memberRepository.findById(loginUserId)
				.orElseThrow(NOT_FOUND_MEMBER::toDomainException);
		DiaryRegistrations memberRegistrations = new DiaryRegistrations(registrationRepository.findAllByMemberId(member.getId()));

		if (note.isPrivate()) {
			if (!memberRegistrations.hasDiaryOf(note) && !note.isOwnedBy(member))
				throw UNAUTHENTICATED.toServiceException();
		}
		boolean isBookmarked = bookmarkRepository.existsById(
				NoteMemberCompositeKey.of(member.getId(), noteId));
		boolean isLiked = likeRepository.existsById(NoteMemberCompositeKey.of(member.getId(), noteId));
		boolean isBlocked = blockQueryService.isBlocked(loginUserId, note.getMember().getId());
		return noteMapper.toNoteViewDto(
				note,
				isBookmarked,
				isLiked,
				isBlocked,
				likeRepository.countByNoteId(noteId));
	}

	/**
	 * 공개된 일기 프리뷰 목록 조회
	 *
	 * @param loginUserId 로그인한 사용자의 ID
	 * @param pageable    페이지네이션
	 * @return 공개된 일기 프리뷰 목록
	 */
	@Override
	public NotePreviewPaginationDto getPublicNotePreviews(Long loginUserId,
	                                                      Pageable pageable) {
		Set<Long> noteReadIds = noteReadRepository.findAllByMemberId(loginUserId).stream()
				.map(noteRead -> noteRead.getId().getNoteId())
				.collect(Collectors.toSet());
		Set<Long> blockedMemberIds = blockQueryService.getBlockedMemberIds(loginUserId);
		Page<Note> notes = noteRepository.findPublicNotes(loginUserId, pageable);
		return noteMapper.toNotePreviewPaginationDto(
				notes.getTotalElements(), notes.stream().map(n ->
								noteMapper.toNotePreviewDto(
										n, noteReadIds.contains(n.getId()),
										blockedMemberIds.contains(n.getMember().getId()),
										noteRepository.findGroupNameByNoteId(n.getId()),
										likeRepository.countByNoteId(n.getId()))) // 빈문자열 처리?
						.collect(Collectors.toList()));
	}

	/**
	 * 일기장의 일기 프리뷰 페이지네이션을 가져옵니다.
	 *
	 * @param loginUserId 로그인한 사용자의 ID
	 * @param diaryId     일기장 ID
	 * @param pageable    페이지 요청
	 * @return 일기장의 일기 프리뷰 페이지네이션
	 */
	@Override
	public NotePreviewPaginationDto getNotePreviewsByDiaryId(Long loginUserId, Long diaryId,
	                                                         Pageable pageable) {
		Set<Long> noteReadIds = noteReadRepository.findAllByMemberId(loginUserId).stream()
				.map(noteRead -> noteRead.getId().getNoteId())
				.collect(Collectors.toSet());
		Set<Long> blockedMemberIds = blockQueryService.getBlockedMemberIds(loginUserId);
		Page<Note> notes = noteRepository.findVisibleNotesByDiaryId(loginUserId, diaryId, pageable);
		return noteMapper.toNotePreviewPaginationDto(
				notes.getTotalElements(), notes.stream().map(n ->
								noteMapper.toNotePreviewDto(
										n, noteReadIds.contains(n.getId()),
										blockedMemberIds.contains(n.getMember().getId()),
										noteRepository.findGroupNameByNoteId(n.getId()),
										likeRepository.countByNoteId(n.getId())))
						.collect(Collectors.toList()));
	}
}
