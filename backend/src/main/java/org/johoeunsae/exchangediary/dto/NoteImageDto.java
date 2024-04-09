package org.johoeunsae.exchangediary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class NoteImageDto {

	private final Integer imageIndex;
	private final String imageUrl;
}
