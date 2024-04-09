package org.johoeunsae.exchangediary.mapper;

import org.johoeunsae.exchangediary.dto.*;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MemberMapper.class})
public abstract class NoteMapper {

	@Autowired
	protected ImageService imageService;

	@AfterMapping
	protected void setImageUrl(Note note, @MappingTarget ImageHolder imageHolder) {
		imageHolder.setImageUrl(imageService.getImageUrl(note.getThumbnailUrl()));
	}

	@Named("toNoteImageDto")
	protected NoteImageDto toNoteImageDto(NoteImage noteImage) {
		if (noteImage == null) {
			return null;
		}

		String imageUrl = imageService.getImageUrl(noteImage.getImageUrl());
		return new NoteImageDto(noteImage.getIndex(), imageUrl);
	}

	@Mapping(target = "noteId", source = "note.id")
	@Mapping(expression = "java(note.getPreview())", target = "preview")
	public abstract MemberNotePreviewDto toMemberNotePreviewDto(Note note, boolean hasRead);

	@Mapping(target = "noteId", source = "note.id")
	@Mapping(expression = "java(note.getPreview())", target = "preview")
	public abstract MyNotePreviewDto toMyNotePreviewDto(Note note);

	@Mapping(target = "noteId", source = "note.id")
	@Mapping(target = "author", source = "note.member")
	@Mapping(target = "imageList", source = "note.noteImages", qualifiedByName = "toNoteImageDtoList")
	public abstract NoteLinkedViewDto toNoteLinkedViewDto(Note note, boolean isBookmarked,
			boolean isLiked,
			Integer likeCount, Long nextNoteId, Long prevNoteId);

	@Mapping(target = "noteId", source = "note.id")
	@Mapping(expression = "java(note.getPreview())", target = "preview")
	@Mapping(target = "author", source = "note.member")
	public abstract NotePreviewDto toNotePreviewDto(Note note, boolean hasRead, boolean isBlocked,
			String groupName, Integer likeCount);

	@Mapping(target = "noteId", source = "note.id")
	@Mapping(expression = "java(note.getPreview())", target = "preview")
	@Mapping(target = "title", source = "note.title")
	@Mapping(target = "groupName", source = "diary.groupName")
	@Mapping(target = "author", source = "note.member")
	@Mapping(target = "createdAt", source = "note.createdAt")
	public abstract  NotePreviewDto toNotePreviewDto(Note note, DiarySimpleInfoDto diary, Member member,
			boolean hasRead, boolean isBlocked, Integer likeCount);

	@Mapping(target = "noteId", source = "note.id")
	@Mapping(source = "note.member", target = "author")
	@Mapping(source = "note.noteImages", target = "imageList", qualifiedByName = "toNoteImageDtoList")
	public abstract NoteViewDto toNoteViewDto(Note note, boolean isBookmarked, boolean isLiked, boolean isBlocked,
			Integer likeCount);

	@Named("toNoteImageDtoList")
	@IterableMapping(qualifiedByName = "toNoteImageDto")
	public abstract List<NoteImageDto> toNoteImageDtoList(List<NoteImage> noteImageList);

	public abstract NotePreviewPaginationDto toNotePreviewPaginationDto(Long totalLength,
			List<NotePreviewDto> result);

	public abstract MemberNotePreviewPaginationDto toMemberNotePreviewPaginationDto(
			Long totalLength,
			List<MemberNotePreviewDto> result);

	public abstract MyNotePreviewPaginationDto toMyNotePreviewPaginationDto(
			Long totalLength,
			List<MyNotePreviewDto> result);
}
