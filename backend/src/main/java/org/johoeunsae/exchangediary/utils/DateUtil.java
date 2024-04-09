package org.johoeunsae.exchangediary.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;

public abstract class DateUtil {
	private static final LocalDateTime INFINITY_DATE = stringToDate("9999-12-31");

	public static LocalDateTime getInfinityDate() {
		return INFINITY_DATE;
	}

	public static LocalDateTime stringToDate(String str) {
		boolean matches = Pattern.matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",
				str);
		if (!matches) {
			throw new RuntimeException("적절하지 않은 날짜 포맷의 String 입니다.");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(str, new ParsePosition(0));
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDateTime getMinDate() {
		return LocalDateTime.of(1999, 1, 1, 0, 0, 0);
	}

	public static LocalDateTime getCurrentDate() {
		return LocalDateTime.now();
	}
}
