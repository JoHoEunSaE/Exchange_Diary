package org.johoeunsae.exchangediary.mapper;

import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.dto.DiaryNoteViewDto;
import org.johoeunsae.exchangediary.dto.DiaryPreviewDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiaryMapper {

	DiaryMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(DiaryMapper.class);

	@Mapping(source = "diary.id", target = "diaryId")
	@Mapping(source = "diary.masterMember.id", target = "masterMemberId")
	@Mapping(source = "coverData", target = "coverData")
	DiaryPreviewDto toDiaryPreviewDto(Diary diary, String coverData);

	DiaryNoteViewDto toDiaryNoteViewDto(NoteViewDto noteViewDto, boolean isLiked,
			boolean isBookmarked, boolean isBlocked,
			Long prevNoteId, Long nextNoteId);
}
