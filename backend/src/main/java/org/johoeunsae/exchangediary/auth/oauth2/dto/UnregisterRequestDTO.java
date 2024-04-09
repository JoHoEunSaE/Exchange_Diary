package org.johoeunsae.exchangediary.auth.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;
import org.johoeunsae.exchangediary.member.domain.OauthType;

@Getter
@ToString
@Schema(name = "UnregisterRequestDTO", description = "탈퇴 회원 social 정보 및 사유")
public class UnregisterRequestDTO {

	@Schema(description = "ID 토큰 or Access 토큰 or Authorization 코드", example = "JWT.토큰.이지롱")
	private final String valid;

	@Schema(description = "Oauth2 타입", example = "KAKAO", allowableValues = "KAKAO, NAVER, GOOGLE, APPLE", defaultValue = "KAKAO")
	private final OauthType oauthType;

	@Schema(description = "디바이스 토큰", example = "디바이스 토큰")
	private final String deviceToken;

	@Schema(description = "탈퇴 사유", example = "탈퇴 사유", implementation = UnregisterReasonDTO.class)
	private final UnregisterReasonDTO unregisterReasonDTO;

	@Builder
	public UnregisterRequestDTO(String valid, OauthType oauthType, String deviceToken,
			UnregisterReasonDTO unregisterReasonDTO) {
		this.valid = valid;
		this.oauthType = oauthType;
		this.deviceToken = deviceToken;
		this.unregisterReasonDTO = unregisterReasonDTO;
	}

	public UnregisterRequestDTO(Oauth2LoginRequestVO vo) {
		this.valid = vo.getValid();
		this.oauthType = vo.getOauthType();
		this.deviceToken = vo.getDeviceToken();
		this.unregisterReasonDTO = null;
	}

	public boolean isAppleOauth() {
		return this.oauthType != null && oauthType.equals(OauthType.APPLE);
	}

	public Oauth2LoginRequestVO toOauth2LoginRequestVO() {
		return Oauth2LoginRequestVO.builder().valid(valid).oauthType(oauthType)
				.deviceToken(deviceToken).build();
	}

	public Oauth2LoginRequestVO toOauth2LoginRequestVO(String memberToken) {
		return Oauth2LoginRequestVO.builder().valid(memberToken).oauthType(oauthType)
				.deviceToken(deviceToken).build();
	}

}
