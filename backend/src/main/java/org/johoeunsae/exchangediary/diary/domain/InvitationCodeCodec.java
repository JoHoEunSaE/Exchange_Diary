package org.johoeunsae.exchangediary.diary.domain;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.status.DiaryExceptionStatus;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class InvitationCodeCodec {

	private static final int MAX_UUID_LENGTH = 8;

	/**
	 * UUID 문자열의 Most Significant Bits와 ID를 Base64 인코딩하여 11자리 UUID를 생성합니다.
	 *
	 * @param id 인코딩할 ID
	 * @return Base64 인코딩된 문자열 코드
	 */
	public static String encodeIdToCode(Long id) {
		log.debug("Called encodeIdToCode id: {}", id);
		UUID uuid = UUID.randomUUID();
		ByteBuffer buffer = ByteBuffer.allocate(
				Long.BYTES + MAX_UUID_LENGTH);
		buffer.putLong(id);
		buffer.putLong(uuid.getMostSignificantBits());
		byte[] bytes = buffer.array();
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	/**
	 * Base64 인코딩된 문자열 코드를 디코딩하고 ID를 추출합니다.
	 *
	 * @param code 디코딩할 문자열 코드
	 * @return 디코딩된 ID
	 */
	public static Long decodeIdFromCode(String code) {
		log.debug("Called decodeIdFromCode code: {}", code);
		try {
			byte[] bytes = Base64.getUrlDecoder().decode(code);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			Long originId = buffer.getLong();
			String originString = new String(bytes, Long.BYTES, bytes.length - Long.BYTES,
					StandardCharsets.UTF_8);
			log.debug("originId: {}", originId);
			log.debug("originString: {}", originString);
			return originId;
		} catch (Exception e) {
			log.error("Failed to decodeIdFromCode code: {}", code, e);
			throw new DomainException(DiaryExceptionStatus.INVALID_INVITATION_CODE);
		}
	}
}
