package org.johoeunsae.exchangediary.utils.obfuscation;

import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * base64로 인코딩된 데이터를 디코딩하는 클래스
 */
@Component
@Deprecated
public class DataBase64Decoder implements DataDecoder {

	private static final Base64.Decoder decoder = java.util.Base64.getDecoder();

	@Override
	public String decode(String encodedData) {
		byte[] decodedBytes = decoder.decode(encodedData);
		return new String(decodedBytes);
	}
}
