package utils.testdouble.auth;

import java.util.Optional;
import lombok.Builder;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;

@Builder
public class TestOauth2LoginInfoVO {

	private static final String DEFAULT_OAUTH_ID = "oauthId";
	private static final String DEFAULT_EMAIL = "test@test.com";
	private static final String DEFUALT_DEVICE_TOKEN = "device_token";
	private static final String DEFAULT_REFRESH_TOKEN = "refreshToken";

	public static Oauth2LoginInfoVO asOauthLoginInfo(OauthType oauthType, String refreshToken) {
		return Oauth2LoginInfoVO.builder()
				.oauthId(DEFAULT_OAUTH_ID)
				.email(DEFAULT_EMAIL)
				.oauthType(oauthType)
				.deviceToken(DEFUALT_DEVICE_TOKEN)
				.refreshToken(Optional.ofNullable(refreshToken))
				.build();
	}

	public static Oauth2LoginInfoVO asNoneSocial() {
		return asOauthLoginInfo(null, null);
	}

	public static Oauth2LoginInfoVO asKakao() {
		return asOauthLoginInfo(OauthType.KAKAO, null);
	}

	public static Oauth2LoginInfoVO asNaver() {
		return asOauthLoginInfo(OauthType.NAVER, null);
	}

	public static Oauth2LoginInfoVO asGoogle() {
		return asOauthLoginInfo(OauthType.GOOGLE, null);
	}

	public static Oauth2LoginInfoVO asApple() {
		return asOauthLoginInfo(OauthType.APPLE, DEFAULT_REFRESH_TOKEN);
	}


}
