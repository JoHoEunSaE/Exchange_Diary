package org.johoeunsae.exchangediary.image.service;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.image.domain.ImageDeleteEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageEventHandler {

	private final ImageService imageService;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void deleteImage(ImageDeleteEvent event) {
		imageService.deleteImage(event.getImageUrl());
	}
}
