package org.johoeunsae.exchangediary.utils.obfuscation;

import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * Base64로 인코딩하는 클래스
 */
@Component
public class DataBase64Encoder implements DataEncoder {

	private static final Base64.Encoder encoder = Base64.getEncoder();

	@Override
	public String encode(String data) {
		return encoder.encodeToString(data.getBytes());
	}
}
