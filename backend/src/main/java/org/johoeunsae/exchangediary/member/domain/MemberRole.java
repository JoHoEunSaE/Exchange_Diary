package org.johoeunsae.exchangediary.member.domain;

/**
 * Description:
 * - MemberRole enum
 * - ADMIN, USER - domain을 위한 enum
 * - S_ADMIN, S_USER - Spring Security를 위한 상수 (role정보를 담은 문자열)
 */
public enum MemberRole {
	// TODO: blacklist user에 대한 처리 필요
	ADMIN, USER, BLACKLIST_USER;
	public static final String S_ADMIN = "ROLE_ADMIN";
	public static final String S_USER = "ROLE_USER";
	public static final String S_BLACKLIST_USER = "ROLE_BLACKLIST_USER";
}
