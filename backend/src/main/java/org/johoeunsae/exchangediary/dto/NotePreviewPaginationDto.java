package org.johoeunsae.exchangediary.dto;

import static org.springframework.data.domain.Sort.Direction.DESC;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "NotePreviewPaginationDto", description = "노트 미리보기 페이지네이션")
@Decodable
public class NotePreviewPaginationDto {

	@Schema(name = "result", description = "노트 미리보기 리스트", implementation = NotePreviewDto.class)
	private final List<NotePreviewDto> result;
	@Schema(name = "totalLength", description = "전체 길이")
	private final Long totalLength;
	@Builder.Default
	private Sort.Direction sort = DESC;
}
