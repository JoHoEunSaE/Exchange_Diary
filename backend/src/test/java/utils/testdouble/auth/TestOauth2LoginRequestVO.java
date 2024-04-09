package utils.testdouble.auth;

import lombok.Builder;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;

@Builder
public class TestOauth2LoginRequestVO {

	public static final String DEFAULT_VALID = "valid";
	public static final String DEFAULT_DEVICE_TOKEN = "deviceToken";

	private final OauthType oauthType;
	@Builder.Default
	private final String valid = DEFAULT_VALID;
	@Builder.Default
	private final String deviceToken = DEFAULT_DEVICE_TOKEN;

	public static Oauth2LoginRequestVO asOauthRequest(OauthType oauthType) {
		return Oauth2LoginRequestVO.builder()
				.valid(DEFAULT_VALID)
				.oauthType(oauthType)
				.deviceToken(DEFAULT_DEVICE_TOKEN)
				.build();
	}

	public static Oauth2LoginRequestVO asNoneSocialRequest() {
		return asOauthRequest(null);
	}

	public static Oauth2LoginRequestVO asKakaoReqeust() {
		return asOauthRequest(OauthType.KAKAO);
	}

	public static Oauth2LoginRequestVO asNaverRequest() {
		return asOauthRequest(OauthType.NAVER);
	}

	public static Oauth2LoginRequestVO asGoogleRequest() {
		return asOauthRequest(OauthType.GOOGLE);
	}


	public static Oauth2LoginRequestVO asAppleRequest() {
		return Oauth2LoginRequestVO.builder()
				.valid(null)
				.oauthType(OauthType.APPLE)
				.deviceToken(DEFAULT_DEVICE_TOKEN)
				.build();
	}
}
