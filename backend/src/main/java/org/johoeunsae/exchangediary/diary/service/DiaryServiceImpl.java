package org.johoeunsae.exchangediary.diary.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.diary.domain.*;
import org.johoeunsae.exchangediary.diary.domain.invitation.Invitation;
import org.johoeunsae.exchangediary.diary.domain.invitation.InvitationManager;
import org.johoeunsae.exchangediary.diary.domain.invitation.InvitationType;
import org.johoeunsae.exchangediary.diary.domain.invitation.code.CodeInvitation;
import org.johoeunsae.exchangediary.diary.repository.CoverColorRepository;
import org.johoeunsae.exchangediary.diary.repository.CoverImageRepository;
import org.johoeunsae.exchangediary.diary.repository.DiaryRepository;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.dto.*;
import org.johoeunsae.exchangediary.image.domain.ImageDeleteEvent;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.mapper.DiaryMapper;
import org.johoeunsae.exchangediary.mapper.NoteMapper;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.service.NoteService;
import org.johoeunsae.exchangediary.notice.domain.event.DiaryMasterChangedEvent;
import org.johoeunsae.exchangediary.notice.domain.event.DiaryMemberKickEvent;
import org.johoeunsae.exchangediary.notice.domain.event.DiaryNewMemberEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus.*;
import static org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus.NOT_FOUND_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class DiaryServiceImpl implements DiaryService {

	private static final int MAX_DIARY_REGISTRATION_COUNT = 5;
	private final DiaryRepository diaryRepository;
	private final CoverImageRepository coverImageRepository;
	private final CoverColorRepository coverColorRepository;
	private final MemberRepository memberRepository;
	private final RegistrationRepository registrationRepository;
	private final InvitationManager invitationManager;
	private final NoteService noteService;
	private final InvitationService invitationService;
	private final DiaryMapper diaryMapper;
	private final NoteMapper noteMapper;
	private final ImageService imageService;
	private final ApplicationEventPublisher eventPublisher;
	@Value("${spring.images.path.diary-cover}")
	private String DIARY_COVER_IMAGE_DIR;

	@Override
	public DiaryPreviewDto createImageCoverDiary(Long loginMemberId, DiaryCreateRequestDto dto) {
		log.debug("Called createImageCoverDiary loginMemberId: {}, dto: {}", loginMemberId, dto);
		Member masterMember = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

		LocalDateTime now = LocalDateTime.now();

		Diary newDiary = Diary.of(
				masterMember,
				now,
				dto.getTitle(),
				dto.getGroupName(),
				dto.getCoverType()
		);
		diaryRepository.save(newDiary);

		imageService.validImageUrl(dto.getCoverData(), DIARY_COVER_IMAGE_DIR);
		String coverImageUrl = imageService.getImageUrl(dto.getCoverData());

		CoverImage coverImage = CoverImage.of(newDiary, dto.getCoverData());
		coverImageRepository.save(coverImage);

		Registration registration = Registration.of(
				masterMember,
				newDiary,
				now
		);

		registrationRepository.save(registration);
		return diaryMapper.toDiaryPreviewDto(newDiary, coverImageUrl);
	}

	@Override
	public DiaryPreviewDto createColorCoverDiary(Long loginMemberId, DiaryCreateRequestDto dto) {
		log.debug("Called createColorCoverDiary loginMemberId: {}, dto: {}", loginMemberId, dto);
		Member masterMember = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

		LocalDateTime now = LocalDateTime.now();
		Diary newDiary = Diary.of(
				masterMember,
				now,
				dto.getTitle(),
				dto.getGroupName(),
				dto.getCoverType()
		);

		newDiary = diaryRepository.save(newDiary);
		coverColorRepository.save(CoverColor.of(newDiary, dto.getCoverData()));

		Registration registration = Registration.of(
				masterMember,
				newDiary,
				now
		);

		registrationRepository.save(registration);
		return diaryMapper.toDiaryPreviewDto(newDiary, dto.getCoverData());
	}

	@Override
	public void deleteDiary(Long diaryId, Long memberId) {
		log.debug("Called deleteDiary diaryId: {}, memberId: {}", diaryId, memberId);
		Member member = memberRepository.findById(memberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);
		if (!diary.isMaster(member)) {
			throw NO_PERMISSION_DELETE.toServiceException();
		}

		if (diary.getCoverType().equals(CoverType.IMAGE)) {
			eventPublisher.publishEvent(new ImageDeleteEvent(diary.getCoverImage().getImageUrl()));
		}
		diaryRepository.delete(diary);
	}

	@Override
	public String editToImageCoverDiary(Diary diary, String coverImageData, CoverType coverType) {
		log.debug("Called editToImageCoverDiary diary: {}, coverImageData: {}, coverType: {}",
				diary, coverImageData, coverType);
		String imageUrl = imageService.parseImageUrl(coverImageData, DIARY_COVER_IMAGE_DIR);
		log.info("imageUrl : {}", imageUrl);
		switch (diary.getCoverType()) {
//	    이미지 커버 일기장 -> 이미지 커버 일기장
			case IMAGE: {
				if (diary.getCoverData().equals(imageUrl)) {
					break;
				}
				if (Objects.nonNull(diary.getCoverImage())) {
					eventPublisher.publishEvent(
							new ImageDeleteEvent(diary.getCoverImage().getImageUrl()));
				}
				imageService.validImageUrl(imageUrl, DIARY_COVER_IMAGE_DIR);
				coverImageRepository.updateCoverImageByDiaryId(diary.getId(), imageUrl);
				break;
			}
//		색상 커버 일기장 -> 이미지 커버 일기장
			case COLOR: {
				coverColorRepository.deleteByDiaryId(diary.getId());
				imageService.validImageUrl(imageUrl, DIARY_COVER_IMAGE_DIR);
				coverImageRepository.save(CoverImage.of(diary, imageUrl));
				break;
			}
		}
		diary.changeCoverType(coverType);
		// 이미지 full url 가져오기
		return imageService.getImageUrl(imageUrl);
	}

	@Override
	public String editToColorCoverDiary(Diary diary, String coverColorCode, CoverType coverType) {
		log.debug("Called editToColorCoverDiary diary: {}, coverColorCode: {}, coverType: {}",
				diary, coverColorCode, coverType);
		switch (diary.getCoverType()) {
//		이미지 커버 일기장 -> 색상 커버 일기장
			case IMAGE: {
				if (Objects.nonNull(diary.getCoverImage())) {
					eventPublisher.publishEvent(
							new ImageDeleteEvent(diary.getCoverImage().getImageUrl()));
				}
				coverImageRepository.deleteByDiaryId(diary.getId());
				coverColorRepository.save(CoverColor.of(diary, coverColorCode));
				break;
			}
//		색상 커버 일기장 -> 색상 커버 일기장
			case COLOR: {
				if (diary.getCoverData().equals(coverColorCode)) {
					break;
				}
				coverColorRepository.updateCoverColorByDiaryId(diary.getId(), coverColorCode);
				break;
			}
		}
		diary.changeCoverType(coverType);
		return coverColorCode;
	}

	@Override
	public DiaryPreviewDto editDiary(Long loginMemberId, Long diaryId, DiaryUpdateRequestDto dto) {
		log.debug("Called editDiary loginMemberId: {}, diaryId: {}, dto: {}",
				loginMemberId, diaryId, dto);

		Member member = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);

		if (!diary.isMaster(member)) {
			throw NO_PERMISSION_EDIT.toServiceException();
		}

		String coverData = null;
		switch (dto.getCoverType()) {
			case IMAGE:
				coverData = editToImageCoverDiary(diary, dto.getCoverData(), dto.getCoverType());
				break;
			case COLOR:
				coverData = editToColorCoverDiary(diary, dto.getCoverData(), dto.getCoverType());
				break;
		}

		diary.updateTitle(dto.getTitle());
		diary.updateGroupName(dto.getGroupName());

		return diaryMapper.toDiaryPreviewDto(diary, coverData);
	}

	@Override
	public void validateInvitationCodeGenerate(Long loginMemberId, Long diaryId) {
		log.debug("Called validateGetInvitationCodeGenerate: loginMemberId={}, diaryId={}",
				loginMemberId, diaryId);
		diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);

		registrationRepository.findByDiaryId(diaryId)
				.stream()
				.filter(registration -> Objects.equals(registration.getMember().getId(),
						loginMemberId))
				.findAny()
				.orElseThrow(NO_PERMISSION_INVITE::toServiceException);
	}

	@Override
	public InvitationCodeDto generateDiaryInvitationCode(Long loginMemberId, Long diaryId) {
		log.debug("Called generateDiaryInvitationCode: loginMemberId={}, diaryId={}",
				loginMemberId, diaryId);
		Invitation invitation = invitationService.findByDiaryId(diaryId)
				.filter(inv -> !inv.isExpired(LocalDateTime.now()))
				.orElseGet(() -> (CodeInvitation) invitationManager.createInvitation(diaryId, loginMemberId, InvitationType.CODE));
		invitationService.saveInvitation(invitation);

		return InvitationCodeDto.builder()
				.invitationCode(invitation.getValue())
				.expiredAt(invitation.getExpiredAt())
				.build();
	}

	@Override
	public Long getDiaryIdByInvitationCode(String invitationCode) {
		log.debug("Called getDiaryIdByInvitationCode: invitationCode={}", invitationCode);
		Invitation invitation = invitationService.getByInvitationCode(invitationCode);
		return invitation.getDiaryId();
	}

	@Override
	public void addMemberToDiary(Long loginMemberId, Long diaryId) {
		log.debug("Called addMemberToDiary loginMemberId: {}, diaryId: {}", loginMemberId, diaryId);
		Member member = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);

		List<Registration> memberRegistrations = registrationRepository.findAllByMemberId(loginMemberId);
		if (memberRegistrations.size() > MAX_DIARY_REGISTRATION_COUNT)
			throw EXCEED_MAX_DIARY_COUNT.toServiceException();
		memberRegistrations.stream()
				.filter(registration -> registration.getDiary().equals(diary))
				.findFirst()
				.ifPresent(registration -> {
					throw ALREADY_REGISTERED_MEMBER.toServiceException();
				});

		Registration registration = Registration.of(member, diary, LocalDateTime.now());

		registrationRepository.save(registration);
		eventPublisher.publishEvent(
				DiaryNewMemberEvent.builder()
						.diaryTitle(diary.getTitle())
						.diaryId(diaryId)
						.newMemberName(member.getNickname())
						.newMemberId(member.getId())
						.createdAt(LocalDateTime.now())
						.build()

		);
	}

	@Override
	public NotePreviewDto createNoteToDiary(Long loginMemberId, Long diaryId, NoteCreateRequestDto dto) {
		log.debug("Called createNoteToDiary loginMemberId: {}, diaryId: {}, dto: {}", loginMemberId,
				diaryId, dto);
		Member member = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);

		if (!diary.isDiaryMember(member)) {
			throw NO_PERMISSION_WRITE_NOTE.toServiceException();
		}

		List<NoteImageCreateDto> imageUrls;
		if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
			imageUrls = dto.getImageUrls().stream()
					.map(image -> NoteImageCreateDto.builder()
							.imageUrl(image)
							.imageIndex(dto.getImageUrls().indexOf(image))
							.build())
					.collect(Collectors.toList());
		} else {
			imageUrls = new ArrayList<>();
		}

		Note note = noteService.createNoteToDiary(loginMemberId, dto.getTitle(), dto.getContent(),
				dto.getVisibleScope(), imageUrls,
				LocalDateTime.now(), diary);
		return noteMapper.toNotePreviewDto(note, false, false, diary.getGroupName(), 0);
	}

	@Override
	public void changeDiaryMaster(Long loginMemberId, Long diaryId, Long targetMemberId) {
		log.debug("Called changeDiaryMaster loginMemberId: {}, diaryId: {}, targetMemberId: {}",
				loginMemberId, diaryId, targetMemberId);
//		일기장이 존재하는지 확인
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);
		Member loginMember = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

//		일기장의 마스터가 맞는지 확인
		if (!diary.isMaster(loginMember)) {
			throw NO_PERMISSION_CHANGE_DIARY_MASTER.toServiceException();
		}

//      대상 멤버가 일기장에 속해있는지 확인
		Member targetMember = memberRepository.findById(targetMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		if (!diary.isDiaryMember(targetMember)) {
			throw NO_DIARY_MEMBER.toServiceException();
		}

//      대상 멤버가 이미 마스터인지 확인
		if (diary.isMaster(targetMember)) {
			throw ALREADY_MASTER_MEMBER.toServiceException();
		}

//      대상 멤버를 마스터로 변경
		diary.changeMaster(targetMember);
		diaryRepository.save(diary);

		eventPublisher.publishEvent(
				DiaryMasterChangedEvent.builder()
						.diaryTitle(diary.getTitle())
						.diaryId(diaryId)
						.changedMasterName(targetMember.getNickname())
						.changedMasterId(targetMemberId)
						.createdAt(LocalDateTime.now())
						.build()
		);
	}

	@Override
	public void leaveDiary(Long loginMemberId, Long diaryId) {
		log.debug("Called leaveDiary loginMemberId: {}, diaryId: {}", loginMemberId, diaryId);
//		일기장이 존재하는지 확인
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);
		Member loginMember = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

//		일기장에 속해있는지 확인
		if (!diary.isDiaryMember(loginMember)) {
			throw NO_DIARY_MEMBER.toServiceException();
		}

//      일기장에 속해있는 본인의 일기들을 일기장에서 떼어내기
		noteService.tearOffMemberNotesFromDiary(loginMemberId, diaryId);

//		일기장 마스터인지 확인
		if (diary.isMaster(loginMember)) {
//			다른 멤버가 존재한다면 마스터를 넘겨주고, 그렇지 않으면 일기장을 삭제
			Optional<Member> newMaster = diary.getRegistrations()
					.stream()
					.map(Registration::getMember)
					.filter(member -> !member.equals(loginMember))
					.findFirst();
			if (newMaster.isPresent()) {
				diary.changeMaster(newMaster.get());
				diary.removeMember(loginMember);
				diaryRepository.save(diary);
			} else {
				diaryRepository.delete(diary);
			}
			return;
		}
		diary.removeMember(loginMember);
		diaryRepository.save(diary);
	}

	@Override
	public void kickDiaryMember(Long loginMemberId, Long diaryId, Long targetMemberId) {
		log.debug("Called kickDiaryMember loginMemberId: {}, diaryId: {}, targetMemberId: {}",
				loginMemberId, diaryId, targetMemberId);
//		일기장이 존재하는지 확인
		Diary diary = diaryRepository.findById(diaryId)
				.orElseThrow(NON_EXIST_DIARY::toServiceException);
		Member loginMember = memberRepository.findById(loginMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);

//		일기장의 마스터가 맞는지 확인
		if (!diary.isMaster(loginMember)) {
			throw NO_PERMISSION_KICK_MEMBER.toServiceException();
		}

//      대상 멤버가 일기장에 속해있는지 확인
		Member targetMember = memberRepository.findById(targetMemberId)
				.orElseThrow(NOT_FOUND_MEMBER::toServiceException);
		if (!diary.isDiaryMember(targetMember)) {
			throw NO_DIARY_MEMBER.toServiceException();
		}

//      자기 자신을 대상으로 하는지 확인
		if (loginMember.equals(targetMember)) {
			throw CANNOT_KICK_SELF.toServiceException();
		}

//      대상 멤버의 일기들을 일기장에서 떼어내기
		noteService.tearOffMemberNotesFromDiary(targetMemberId, diaryId);

//      대상 멤버를 일기장에서 추방
		diary.removeMember(targetMember);
		diaryRepository.save(diary);

		eventPublisher.publishEvent(
				DiaryMemberKickEvent.builder()
						.diaryTitle(diary.getTitle())
						.diaryId(diaryId)
						.receiverId(targetMemberId)
						.createdAt(LocalDateTime.now())
						.build()
		);
	}
}
