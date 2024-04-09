package org.johoeunsae.exchangediary.utils.obfuscation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Base64;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DecodeSerializer extends JsonSerializer<String> {

	private static final Base64.Decoder decoder = java.util.Base64.getDecoder();

	/*
		private static boolean isEncoded(String data) {
			Pattern pattern = Pattern.compile(
					"^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
			Matcher matcher = pattern.matcher(data);
			return matcher.find();
		}
	*/
	public static String decodeData(String encodedData) {
//		if (!isEncoded(encodedData)) {
//			return encodedData;
//		}

		byte[] decodedBytes;
		try {
			decodedBytes = decoder.decode(encodedData);
		} catch (IllegalArgumentException e) {
			log.error("Error occurred while decoding data: {}", e.getMessage());
			return encodedData;
		}
		return new String(decodedBytes);
	}

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		gen.writeString(decodeData(value));
	}
}
