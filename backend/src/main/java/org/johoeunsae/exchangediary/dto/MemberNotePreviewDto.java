package org.johoeunsae.exchangediary.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;
import org.johoeunsae.exchangediary.utils.obfuscation.DecodeSerializer;

@AllArgsConstructor
@Getter
@Schema(description = "일기 목록 조회 시 반환되는 Dto")
@Decodable
public class MemberNotePreviewDto implements ImageHolder {

	@Schema(description = "일기 id", defaultValue = "1")
	private final Long noteId;

	@Schema(description = "일기장 ID", example = "1")
	private final Long diaryId;

	@Schema(description = "일기 제목", defaultValue = "삶과 죽음에 관하여...")
	@JsonSerialize(using = DecodeSerializer.class)
	private final String title;

	@Schema(description = "일기 내용 미리보기", defaultValue = "삶은 계란이다. 죽음은 ...")
	private final String preview;

	@Schema(description = "일기 썸네일 url", defaultValue = "https://exchangediary.s3.ap-northeast-2.amazonaws.com/...")
	private String thumbnailUrl;

	@Schema(description = "일기 작성 시간", defaultValue = "2021-08-01T00:00:00")
	private final LocalDateTime createdAt;

	@Schema(description = "글을 읽었는지 여부", defaultValue = "false")
	private final boolean hasRead;

	@Override
	public void setImageUrl(String imageUrl) {
		this.thumbnailUrl = imageUrl;
	}
}
