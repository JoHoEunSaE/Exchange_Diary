package org.johoeunsae.exchangediary.mapper;

import org.johoeunsae.exchangediary.dto.NoticeDto;
import org.johoeunsae.exchangediary.notice.domain.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
	NoticeMapper INSTANCE = getMapper(NoticeMapper.class);

	@Named("toNoticeDto")
	NoticeDto toNoticeDto(Notice notice);
}
