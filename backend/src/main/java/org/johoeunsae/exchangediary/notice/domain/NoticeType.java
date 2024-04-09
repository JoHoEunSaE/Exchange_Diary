package org.johoeunsae.exchangediary.notice.domain;

import lombok.Getter;

/**
 * 알림 타입을 정의하는 클래스입니다.
 *
 * <p>
 * 알림 타입은 [도메인]_[내용]_[FROM]_[TO?]의 구조로 이뤄집니다. NoticeType은 각 알림 타입에 대한 이름, 제목 및 내용 형식을 지정합니다. 또한, 알림
 * 타입에 따라 내용을 생성하는 기능을 제공합니다.
 * </p>
 */
@Getter
public enum NoticeType {
	ANNOUNCEMENT,
	DIARY_NOTE_FROM_TO,
	DIARY_MEMBER_FROM_TO,
	DIARY_MEMBER_KICK_FROM,
	NOTE_LIKE_FROM_TO,
	FOLLOW_CREATE_FROM, DIARY_MASTER_CHANGED_TO,
}
