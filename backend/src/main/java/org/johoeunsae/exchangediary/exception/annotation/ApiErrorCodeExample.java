package org.johoeunsae.exchangediary.exception.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.johoeunsae.exchangediary.exception.status.AuthExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.BlacklistExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.BlockExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.BookmarkExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.CloudExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.FollowExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.LikeExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoteExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.NoticeExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.ReportExceptionStatus;
import org.johoeunsae.exchangediary.exception.status.UtilsExceptionStatus;

/**
 * // @formatter:off
 * Swagger 에러코드 예시를 명세하기 위한 커스텀 어노테이션입니다.
 * 해당 컨트롤러 메소드 위에 작성합니다.
 * 해당 API에서 발생할 수 있는 예외의 종류를 도메인 별로 명세합니다.
 * ex. 어떤 API에서 AuthExceptionStatus.UNAUTHORIZED, MemberExceptionStatus.NOT_FOUND_MEMBER
 * 예외가 발생할 수 있다면
 *
 *        @ApiErrorCodeExample(
 *            authExceptionStatuses = {
 * 					AuthExceptionStatus.UNAUTHORIZED,
 *            },
 *            memberExceptionStatuses = {
 *                  MemberExceptionStatus.NOT_FOUND_MEMBER,
 *            }
 *        )
 * 형태로 작성합니다.
 * // @formatter:on
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodeExample {

	AuthExceptionStatus[] authExceptionStatuses() default {};

	BlacklistExceptionStatus[] blacklistExceptionStatuses() default {};

	BlockExceptionStatus[] blockExceptionStatuses() default {};

	BookmarkExceptionStatus[] bookmarkExceptionStatuses() default {};

	CloudExceptionStatus[] cloudExceptionStatuses() default {};

	DiaryExceptionStatus[] diaryExceptionStatuses() default {};

	FollowExceptionStatus[] followExceptionStatuses() default {};

	LikeExceptionStatus[] likeExceptionStatuses() default {};

	NoteExceptionStatus[] noteExceptionStatuses() default {};

	NoticeExceptionStatus[] noticeExceptionStatuses() default {};

	ReportExceptionStatus[] reportExceptionStatuses() default {};

	UtilsExceptionStatus[] utilsExceptionStatuses() default {};

	MemberExceptionStatus[] memberExceptionStatuses() default {};
}
