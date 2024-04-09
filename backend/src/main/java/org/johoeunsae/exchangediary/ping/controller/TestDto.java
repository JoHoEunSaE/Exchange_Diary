package org.johoeunsae.exchangediary.ping.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@FieldNameConstants
public class TestDto {
	private int hello;
	private String content;
	private String title;
	private List<String> list;
}
