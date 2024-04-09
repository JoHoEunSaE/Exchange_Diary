package org.johoeunsae.exchangediary.message;

public abstract class AuthResponseMessages {
    public final static String OAUTH2_FAILURE = "OAuth2 인증에 실패했습니다.";
    public final static String JWT_ACCESS_FAILURE = "해당 토큰으로 접근할 수 없습니다.";
    public final static String JWT_AUTH_FAILURE = "해당 토큰이 인증되지 않았습니다.";
}
