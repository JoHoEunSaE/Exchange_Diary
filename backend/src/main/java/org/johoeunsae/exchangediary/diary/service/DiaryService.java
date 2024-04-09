package org.johoeunsae.exchangediary.diary.service;

import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.dto.*;

public interface DiaryService {

	/**
	 * 일기장 초대 코드를 생성할 수 있는지 검증합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param diaryId       일기장 ID
	 */
	void validateInvitationCodeGenerate(Long loginMemberId, Long diaryId);

	/**
	 * 일기장 초대 코드를 생성합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param diaryId       일기장 ID
	 * @return 생성된 초대 코드 정보 {@link InvitationCodeDto}
	 */
	InvitationCodeDto generateDiaryInvitationCode(Long loginMemberId, Long diaryId);

	/**
	 * 이미지 커버를 가지는 일기장을 생성합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param dto           {@link DiaryCreateRequestDto} 일기장 생성 요청 DTO
	 * @return 생성된 일기장 정보 {@link DiaryPreviewDto}
	 */
	DiaryPreviewDto createImageCoverDiary(Long loginMemberId, DiaryCreateRequestDto dto);

	/**
	 * 컬러 커버를 가지는 일기장을 생성합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param dto           {@link DiaryCreateRequestDto} 일기장 생성 요청 DTO
	 * @return 생성된 일기장 정보 {@link DiaryPreviewDto}
	 */
	DiaryPreviewDto createColorCoverDiary(Long loginMemberId, DiaryCreateRequestDto dto);

	/**
	 * 일기장을 삭제합니다.
	 *
	 * @param diaryId  일기장 ID
	 * @param memberId 삭제를 요청한 멤버 ID
	 */
	void deleteDiary(Long diaryId, Long memberId);

	/**
	 * 일기장의 이미지 커버를 수정합니다.
	 *
	 * @param diary          수정할 일기장
	 * @param coverImageData 수정할 이미지 커버 데이터
	 * @param coverType      수정할 커버 타입
	 */
	String editToImageCoverDiary(Diary diary, String coverImageData, CoverType coverType);

	/**
	 * 일기장의 컬러 커버를 수정합니다.
	 *
	 * @param diary          수정할 일기장
	 * @param coverColorCode 수정할 커버 색상 코드
	 * @param coverType      수정할 커버 타입
	 */
	String editToColorCoverDiary(Diary diary, String coverColorCode, CoverType coverType);

	/**
	 * 일기장을 수정합니다. 수정된 일기장 정보를 반환합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param diaryId       수정할 일기장 ID
	 * @param dto           {@link DiaryUpdateRequestDto} 일기장 수정 요청 DTO
	 * @return 수정된 일기장
	 */
	DiaryPreviewDto editDiary(Long loginMemberId, Long diaryId, DiaryUpdateRequestDto dto);

	/**
	 * 일기장 초대 코드로 일기장 ID를 조회합니다.
	 *
	 * @param invitationCode 일기장 초대 코드
	 * @return 일기장 ID
	 */
	Long getDiaryIdByInvitationCode(String invitationCode);

	/**
	 * 멤버를 일기장에 추가합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param diaryId       일기장 ID
	 */
	void addMemberToDiary(Long loginMemberId, Long diaryId);

	/**
	 * 일기장에 일기를 생성합니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param diaryId       일기장 ID
	 * @param dto           {@link NoteCreateRequestDto} 일기 생성 요청 DTO
	 * @return
	 */
	NotePreviewDto createNoteToDiary(Long loginMemberId, Long diaryId, NoteCreateRequestDto dto);

	/**
	 * 일기장의 마스터를 변경합니다.
	 *
	 * @param loginMemberId  로그인한 멤버 ID
	 * @param diaryId        일기장 ID
	 * @param targetMemberId 마스터로 변경할 멤버 ID
	 */
	void changeDiaryMaster(Long loginMemberId, Long diaryId, Long targetMemberId);

	/**
	 * 일기장에서 떠납니다.
	 *
	 * @param loginMemberId 로그인한 멤버 ID
	 * @param diaryId       일기장 ID
	 */
	void leaveDiary(Long loginMemberId, Long diaryId);

	/**
	 * 일기장에서 멤버를 추방합니다.
	 *
	 * @param loginMemberId  로그인한 멤버 ID
	 * @param diaryId        일기장 ID
	 * @param targetMemberId 추방할 멤버 ID
	 */
	void kickDiaryMember(Long loginMemberId, Long diaryId, Long targetMemberId);
}
