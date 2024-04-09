package utils.testdouble.member;

import lombok.Builder;
import org.johoeunsae.exchangediary.dto.MemberUpdateRequestDto;

@Builder
public class TestMemberUpdateRequestDto {

	public static final String DEFAULT_NICKNAME = "nickName";
	public static final String DEFAULT_STATEMENT = "statement";
	public static final String DEFAULT_PROFILE_IMAGE_URL = "profile-images/profile-image.png";

	@Builder.Default
	private String nickname = DEFAULT_NICKNAME;
	@Builder.Default
	private String statement = DEFAULT_STATEMENT;
	@Builder.Default
	private String profileImageUrl = DEFAULT_PROFILE_IMAGE_URL;

	public static MemberUpdateRequestDto asDefaultDto() {
		return MemberUpdateRequestDto.builder()
				.nickname(DEFAULT_NICKNAME)
				.statement(DEFAULT_STATEMENT)
				.profileImageUrl(DEFAULT_PROFILE_IMAGE_URL)
				.build();
	}

	public static MemberUpdateRequestDto asDefaultDto(String nickname) {
		return MemberUpdateRequestDto.builder()
				.nickname(nickname)
				.statement(DEFAULT_STATEMENT)
				.profileImageUrl(DEFAULT_PROFILE_IMAGE_URL)
				.build();
	}


}
