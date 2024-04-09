package org.johoeunsae.exchangediary.auth.jwt;

public class JwtProperties {
    public static final String ROLES = "roles";
    public static final String SCOPES = "scopes";
    public static final String USER_ID = "userId";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final long COMMON_ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7L; // 7일
    public static final long COMMON_REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 30L; // 30일

}
