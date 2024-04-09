package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "MemberNotePreviewPaginationDto", description = "회원 노트 미리보기 페이지네이션")
@Decodable
public class MemberNotePreviewPaginationDto {

	@Schema(name = "result", description = "회원 노트 미리보기 리스트", implementation = MemberNotePreviewDto.class)
	private final List<MemberNotePreviewDto> result;

	@Schema(name = "totalLength", description = "전체 길이")
	private final Long totalLength;
}
