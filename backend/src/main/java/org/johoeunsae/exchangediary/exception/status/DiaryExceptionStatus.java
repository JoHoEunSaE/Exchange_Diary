package org.johoeunsae.exchangediary.exception.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.ControllerException;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.utils.ErrorReason;
import org.springframework.http.HttpStatus;

/**
 * 일기장 도메인 관련 예외 상태를 정의합니다.
 *
 * @see org.johoeunsae.exchangediary.exception.status.ExceptionStatus
 */
@AllArgsConstructor
@ToString
public enum DiaryExceptionStatus implements ExceptionStatus {

	/*{ 일기장 생성 관련 오류  */
	EMPTY_COVER_COLOR_CODE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY001",
			Messages.EMPTY_COVER_COLOR_CODE_VALUE)),
	INVALID_COVER_COLOR_CODE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY002",
			Messages.INVALID_COVER_COLOR_CODE_VALUE)),
	EMPTY_COVER_IMAGE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY003",
			Messages.EMPTY_COVER_IMAGE_VALUE)),
	INVALID_COVER_IMAGE_EXTENSION(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY004",
			Messages.INVALID_COVER_IMAGE_EXTENSION_VALUE)),
	EXCEED_COVER_IMAGE_SIZE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY005",
			Messages.EXCEED_COVER_IMAGE_SIZE_VALUE)),
	INVALID_DIARY_TITLE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY006",
			Messages.DIARY_TITLE_LENGTH_VALUE)),
	DIARY_TITLE_LENGTH(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY007",
			Messages.INVALID_DIARY_TITLE_VALUE)),
	DIARY_GROUP_NAME_LENGTH(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY008",
			Messages.DIARY_GROUP_NAME_LENGTH_VALUE)),
	INVALID_DIARY_GROUP_NAME(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY009",
			Messages.INVALID_DIARY_GROUP_NAME_VALUE)),
	/*} 일기장 생성 관련 오류 */

	NON_EXIST_DIARY(new ErrorReason(HttpStatus.NOT_FOUND.value(), "DIARY010",
			Messages.NON_EXIST_DIARY_VALUE)),
	NO_PERMISSION_DELETE(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY011",
			Messages.NO_PERMISSION_DELETE_VALUE)),
	NO_PERMISSION_EDIT(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY012",
			Messages.NO_PERMISSION_UPDATE_VALUE)),

	/* { 초대 관련 오류 */
	NO_PERMISSION_INVITE(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY013",
			Messages.NO_PERMISSION_INVITE_VALUE)),
	INVALID_INVITATION_CODE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY014",
			Messages.INVALID_INVITATION_CODE_VALUE)),
	ALREADY_REGISTERED_MEMBER(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY015",
			Messages.ALREADY_REGISTERED_MEMBER_VALUE)),
	/* } 초대 관련 오류 */

	/* { 일기장 일기 관련 오류 */
	INVALID_NOTE_TITLE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY016",
			Messages.INVALID_NOTE_TITLE_VALUE)),
	NOTE_TITLE_LENGTH(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY017",
			Messages.NOTE_TITLE_LENGTH_VALUE)),
	INVALID_NOTE_CONTENT(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY018",
			Messages.INVALID_NOTE_CONTENT_VALUE)),
	NOTE_CONTENT_LENGTH(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY019",
			Messages.NOTE_CONTENT_LENGTH_VALUE)),
	EXCEED_NOTE_IMAGE_SIZE(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY020",
			Messages.EXCEED_NOTE_IMAGE_SIZE_VALUE)),

	INVALID_NOTE_IMAGE_EXTENSION(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY021",
			Messages.INVALID_NOTE_IMAGE_EXTENSION_VALUE)),

	NO_PERMISSION_WRITE_NOTE(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY022",
			Messages.NO_PERMISSION_WRITE_NOTE_VALUE)),

	NO_PERMISSION_TEAR_OFF_NOTE(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY023",
			Messages.NO_PERMISSION_TEAR_OFF_NOTE_VALUE)),

	NOTE_NOT_BELONG_TO_DIARY(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY024",
			Messages.NOTE_NOT_BELONG_TO_DIARY_VALUE)),

	NO_PERMISSION_CHANGE_DIARY_MASTER(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY025",
			Messages.NO_PERMISSION_CHANGE_DIARY_MASTER_VALUE)),

	NO_DIARY_MEMBER(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY026",
			Messages.NO_DIARY_MEMBER_VALUE)),

	ALREADY_MASTER_MEMBER(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY027",
			Messages.ALREADY_MASTER_MEMBER_VALUE)),

	NON_REGISTERED_MEMBER(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY028",
			Messages.NON_REGISTERED_MEMBER_VALUE)),

	NO_PERMISSION_KICK_MEMBER(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY029",
			Messages.NO_PERMISSION_KICK_MEMBER_VALUE)),

	CANNOT_KICK_SELF(new ErrorReason(HttpStatus.FORBIDDEN.value(), "DIARY030",
			Messages.CANNOT_KICK_SELF_VALUE)),
	/* } 일기장 일기 관련 오류 */

	EXCEED_MAX_DIARY_COUNT(new ErrorReason(HttpStatus.BAD_REQUEST.value(), "DIARY031",
			Messages.EXCEED_MAX_DIARY_COUNT)),
	;
	private final ErrorReason errorReason;

	public static ExceptionStatus findByErrorCode(String code) {
		for (DiaryExceptionStatus status : DiaryExceptionStatus.values()) {
			if (status.getErrorReason().getCode().equals(code)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public ControllerException toControllerException() {
		return new ControllerException(this);
	}

	@Override
	public ServiceException toServiceException() {
		return new ServiceException(this);
	}

	@Override
	public DomainException toDomainException() {
		return new DomainException(this);
	}

	@Override
	public ErrorReason getErrorReason() {
		return this.errorReason;
	}

	public static abstract class Messages {

		public static final String EMPTY_COVER_COLOR_CODE_VALUE = "일기장 커버 색상 코드 정보가 없습니다.";
		public static final String INVALID_COVER_COLOR_CODE_VALUE = "일기장 커버 색상 코드는 9자리로 작성해 주세요. ex) #000000FF ";
		public static final String EMPTY_COVER_IMAGE_VALUE = "일기장 커버 이미지 정보가 비어있습니다.";
		public static final String INVALID_COVER_IMAGE_EXTENSION_VALUE = "허용되지 않은 이미지 파일 확장자입니다.";
		public static final String EXCEED_COVER_IMAGE_SIZE_VALUE = "이미지 파일의 크기가 너무 큽니다.";
		public static final String DIARY_TITLE_LENGTH_VALUE = "일기장 제목이 너무 깁니다.";
		public static final String INVALID_DIARY_TITLE_VALUE = "일기장 제목이 올바르지 않습니다.";
		public static final String DIARY_GROUP_NAME_LENGTH_VALUE = "그룹 이름이 너무 깁니다.";
		public static final String INVALID_DIARY_GROUP_NAME_VALUE = "그룹 이름은 한글, 영문, 숫자만 입력 가능합니다.";
		public static final String NON_EXIST_DIARY_VALUE = "존재하지 않는 일기장입니다.";
		public static final String NO_PERMISSION_DELETE_VALUE = "일기장을 삭제할 권한이 없습니다.";
		public static final String NO_PERMISSION_UPDATE_VALUE = "일기장을 수정할 권한이 없습니다.";
		public static final String NO_PERMISSION_INVITE_VALUE = "일기장에 초대할 권한이 없습니다.";
		public static final String INVALID_INVITATION_CODE_VALUE = "유효하지 않은 초대 코드입니다.";
		public static final String ALREADY_REGISTERED_MEMBER_VALUE = "이미 일기장에 등록된 회원입니다.";
		public static final String NON_REGISTERED_MEMBER_VALUE = "일기장에 등록되지 않은 회원입니다.";
		public static final String INVALID_NOTE_TITLE_VALUE = "일기 제목이 올바르지 않습니다.";
		public static final String NOTE_TITLE_LENGTH_VALUE = "일기 제목이 너무 깁니다.";
		public static final String INVALID_NOTE_CONTENT_VALUE = "일기 내용이 올바르지 않습니다.";
		public static final String NOTE_CONTENT_LENGTH_VALUE = "일기 내용이 너무 깁니다.";
		public static final String EXCEED_NOTE_IMAGE_SIZE_VALUE = "이미지 파일의 크기가 너무 큽니다.";
		public static final String INVALID_NOTE_IMAGE_EXTENSION_VALUE = "허용되지 않은 이미지 파일 확장자입니다.";
		public static final String NO_PERMISSION_WRITE_NOTE_VALUE = "일기를 작성할 권한이 없습니다.";
		public static final String NO_PERMISSION_TEAR_OFF_NOTE_VALUE = "일기를 떼어낼 권한이 없습니다.";
		public static final String NOTE_NOT_BELONG_TO_DIARY_VALUE = "해당 일기는 해당 일기장에 속하지 않습니다.";
		public static final String NO_PERMISSION_CHANGE_DIARY_MASTER_VALUE = "일기장의 마스터를 변경할 권한이 없습니다.";
		public static final String NO_DIARY_MEMBER_VALUE = "해당 일기장에 속한 멤버가 아닙니다.";
		public static final String ALREADY_MASTER_MEMBER_VALUE = "이미 마스터인 멤버입니다.";
		public static final String NO_PERMISSION_KICK_MEMBER_VALUE = "일기장에서 멤버를 추방할 권한이 없습니다.";
		public static final String CANNOT_KICK_SELF_VALUE = "자기 자신을 추방할 수 없습니다.";
		public static final String EXCEED_MAX_DIARY_COUNT = "최대 일기장 가입 수를 초과했습니다.";
	}
}
