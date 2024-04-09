package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.message.ValidationMessage;
import org.johoeunsae.exchangediary.report.domain.ReportType;
import org.johoeunsae.exchangediary.report.validation.ReasonValidation;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "신고 요청")
public class ReportRequestDto {
	@Schema(description = "신고 사유", example = "SPAM, ADULT, ETC")
	@NotBlank(message = ValidationMessage.NOT_BLANK)
	private final ReportType reportType;
	@Schema(description = "신고 이유", example = "심한 욕설 사용")
	@ReasonValidation
	private final String reason;
}
