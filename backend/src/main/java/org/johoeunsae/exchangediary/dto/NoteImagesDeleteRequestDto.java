package org.johoeunsae.exchangediary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class NoteImagesDeleteRequestDto {
	List<Integer> imageIndexes;
}
