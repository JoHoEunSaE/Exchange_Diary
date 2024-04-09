package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "MemberPreviewPaginationDto", description = "회원 미리보기 페이지네이션")
@FieldNameConstants
public class MemberPreviewPaginationDto {
	@Schema(name = "result", description = "회원 미리보기 리스트")
	private final List<MemberPreviewDto> result;
	@Schema(name = "totalLength", description = "전체 길이")
	private final Long totalLength;
}
