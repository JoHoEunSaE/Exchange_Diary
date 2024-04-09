package org.johoeunsae.exchangediary.dto;


import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.johoeunsae.exchangediary.member.validation.NicknameValidation;
import org.johoeunsae.exchangediary.member.validation.StatementValidation;
import org.johoeunsae.exchangediary.message.ValidationMessage;
import org.springframework.web.multipart.MultipartFile;

@Builder @AllArgsConstructor @Getter
public class MemberCreateRequestDto {

	@NicknameValidation
	@NotBlank(message = ValidationMessage.NOT_BLANK)
	private final String nickname;

	@StatementValidation
	private final String statement;

	private final MultipartFile profileImageData; //BLOB
}
