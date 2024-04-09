package org.johoeunsae.exchangediary.auth.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.johoeunsae.exchangediary.auth.oauth2.domain.WithdrawalReason;

@ToString
@Getter
@Schema(name = "UnregisterReasonDTO", description = "탈퇴 사유 dto")
public class UnregisterReasonDTO {

	@Schema(name = "reason", description = "탈퇴사유 보기", implementation = WithdrawalReason.class)
	private final WithdrawalReason reason;
	@Schema(name = "otherReason", description = "탈퇴사유 직접입력(기타를 눌렀을경우)", example = "직접입력")
	private final String otherReason;

	@Builder
	public UnregisterReasonDTO(WithdrawalReason reason, String otherReason) {
		this.reason = reason;
		this.otherReason = otherReason;
	}

	public String getReason() {
		return reason.getReason();
	}
}
