package org.johoeunsae.exchangediary.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JsonUtil {
	public static String urlEncode(String string) {
		return URLEncoder.encode(string, StandardCharsets.UTF_8);
	}
}
