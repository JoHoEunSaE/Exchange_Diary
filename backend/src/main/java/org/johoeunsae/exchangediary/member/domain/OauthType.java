package org.johoeunsae.exchangediary.member.domain;

public enum OauthType {
    GOOGLE,
    NAVER,
    KAKAO,
    APPLE;

    public static OauthType of(String value) {
        return OauthType.valueOf(value.toUpperCase());
    }
}
