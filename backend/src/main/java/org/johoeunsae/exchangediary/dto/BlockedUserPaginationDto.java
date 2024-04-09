package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(name = "BlockedUserPaginationDto", description = "차단한 멤버 페이지네이션")
public class BlockedUserPaginationDto {

	@Schema(name = "result", description = "차단한 멤버 리스트", implementation = BlockedUserDto.class)
	private final List<BlockedUserDto> result;

	@Schema(name = "totalLength", description = "전체 길이")
	private final Long totalLength;
}
