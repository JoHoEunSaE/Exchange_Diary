package org.johoeunsae.exchangediary.notice.domain;

import lombok.AllArgsConstructor;
import org.johoeunsae.exchangediary.utils.JsonUtil;

@AllArgsConstructor
public class NoticeParameter {
	public enum NoticeParameterType {
		MEMBER("MBR"),
		DIARY("DRY"),
		NOTE("NTE"),
		;

		private final String prefix;

		NoticeParameterType(String prefix) {
			this.prefix = prefix;
		}
	}

	private static final String DELIMITER = "|";
	private static final String LEFT_BRACKET = "{";
	private static final String RIGHT_BRACKET = "}";

	private final NoticeParameterType type;
	private final String name;
	private final Long id;

	public static String getDeepLinkFormat() {
		return LEFT_BRACKET + "%s" + DELIMITER + "%d" + RIGHT_BRACKET;
	}

	public static String getEncodedFormat() {
		return LEFT_BRACKET + "%s" + DELIMITER + "%s" + DELIMITER + "%s" + RIGHT_BRACKET;
	}

	public String getEncodedString() {
		return String.format(
				getEncodedFormat(),
				encoded(type.prefix), encoded(id.toString()), encoded(name)
		);
	}

	private static String encoded(String s) {
		return JsonUtil.urlEncode(s);
	}
}
