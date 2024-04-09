package utils.testdouble.auth;

import lombok.Builder;
import org.johoeunsae.exchangediary.auth.oauth2.domain.WithdrawalReason;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterReasonDTO;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterRequestDTO;
import org.johoeunsae.exchangediary.member.domain.OauthType;

@Builder
public class TestUnregisterRequestDTO {

	public static final String DEFAULT_VALID = "valid";
	public static final String DEFAULT_DEVICE_TOKEN = "deviceToken";

	private final OauthType oauthType;
	@Builder.Default
	private final String valid = DEFAULT_VALID;
	@Builder.Default
	private final String deviceToken = DEFAULT_DEVICE_TOKEN;
	@Builder.Default
	private final UnregisterReasonDTO unregisterReasonDTO = UnregisterReasonDTO.builder().reason(
			WithdrawalReason.OTHER_REASON).build();

	public static UnregisterRequestDTO asOauthRequest(OauthType oauthType) {
		return UnregisterRequestDTO.builder()
				.valid(DEFAULT_VALID)
				.oauthType(oauthType)
				.deviceToken(DEFAULT_DEVICE_TOKEN)
				.build();
	}

	public static UnregisterRequestDTO asNoneSocialRequest() {
		return asOauthRequest(null);
	}

	public static UnregisterRequestDTO asKakaoReqeust() {
		return asOauthRequest(OauthType.KAKAO);
	}

	public static UnregisterRequestDTO asNaverRequest() {
		return asOauthRequest(OauthType.NAVER);
	}

	public static UnregisterRequestDTO asGoogleRequest() {
		return asOauthRequest(OauthType.GOOGLE);
	}


	public static UnregisterRequestDTO asAppleRequest() {
		return UnregisterRequestDTO.builder()
				.valid(null)
				.oauthType(OauthType.APPLE)
				.deviceToken(DEFAULT_DEVICE_TOKEN)
				.build();
	}
}
