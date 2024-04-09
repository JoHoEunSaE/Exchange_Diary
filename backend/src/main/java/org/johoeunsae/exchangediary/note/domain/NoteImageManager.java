package org.johoeunsae.exchangediary.note.domain;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.dto.NoteImageCreateDto;
import org.johoeunsae.exchangediary.image.domain.ImageDeleteEvent;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.note.repository.NoteImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 일기장의 이미지 파일을 관리하는 클래스
 */
@Component
@Transactional
@RequiredArgsConstructor
public class NoteImageManager {
	private final NoteImageRepository noteImageRepository;
	private final ImageService imageService;
	private final ApplicationEventPublisher eventPublisher;

	@Value("${spring.images.path.note}")
	public String NOTE_IMAGE_DIR;

	public void deleteAllImages(Note note) {
		List<NoteImage> images = note.getNoteImages();
		for (NoteImage image : images) {
			eventPublisher.publishEvent(new ImageDeleteEvent(image.getImageUrl()));
			noteImageRepository.delete(image);
		}
	}

	public void deleteImages(Note note, List<Integer> indexes) {
		List<NoteImage> images = note.getNoteImages();

		for (NoteImage image : images) {
			if (indexes.contains(image.getIndex())) {
				eventPublisher.publishEvent(new ImageDeleteEvent(image.getImageUrl()));
				noteImageRepository.delete(image);
			}
		}
		List<NoteImage> remainingImages = IntStream.range(0, images.size())
				.filter(i -> !indexes.contains(i))
				.mapToObj(images::get)
				.collect(Collectors.toList());
		reindexImages(remainingImages);
		note.setNoteImages(remainingImages);
	}

	private void reindexImages(List<NoteImage> images) {
		for (NoteImage image : images) {
			image.specifyIndex(images.indexOf(image));
		}
	}

	public void addImagesToNote(Note note, List<NoteImageCreateDto> imageData) {
		List<NoteImage> noteImages = imageData.stream()
				.map(image -> {
					imageService.validImageUrl(image.getImageUrl(), NOTE_IMAGE_DIR);
					return NoteImage.of(note, image.getImageIndex(), image.getImageUrl());
				}).collect(Collectors.toList());
		noteImageRepository.saveAll(noteImages);
	}
}
