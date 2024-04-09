package org.johoeunsae.exchangediary.dto.entity;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class Board {
	private final String title;
	private final String content;
	private Board(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public static Board of(String title, String content) {
		return new Board(title, content);
	}
}
