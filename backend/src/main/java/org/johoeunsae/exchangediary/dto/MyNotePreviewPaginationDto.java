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
@Decodable
@Schema(name = "MyNotePreviewPaginationDto", description = "자기 노트 미리보기 페이지네이션")
public class MyNotePreviewPaginationDto {

	@Schema(name = "result", description = "자기 노트 미리보기 리스트", implementation = MyNotePreviewDto.class)
	private final List<MyNotePreviewDto> result;

	@Schema(name = "totalLength", description = "전체 길이")
	private final Long totalLength;
}
