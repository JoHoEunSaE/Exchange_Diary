package org.johoeunsae.exchangediary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.utils.obfuscation.Decodable;

@Builder
@AllArgsConstructor
@Getter
@Decodable
@FieldNameConstants
@Schema(name = "DiaryPreviewDto", description = "일기장 미리보기")
public class DiaryPreviewDto {

	@Schema(name = "diaryId", description = "일기장 ID", example = "1")
	private final Long diaryId;
	@Schema(name = "title", description = "일기장 제목", example = "제목")
	private final String title;
	@Schema(name = "groupName", description = "일기장 그룹명", example = "그룹명")
	private final String groupName;
	@Schema(name = "coverData", description = "일기장 커버 데이터", example = "[이미지 주소] or [컬러 코드]")
	private final String coverData;
	@Schema(name = "coverType", description = "일기장 커버 타입", example = "COLOR")
	private final CoverType coverType; // ENUM
	@Schema(name = "masterMemberId", description = "일기장 마스터 멤버 ID", example = "1")
	private final Long masterMemberId;
	@Schema(name = "createdAt", description = "일기장 생성일", example = "2023-06-03T00:00:00")
	private final LocalDateTime createdAt;
}
