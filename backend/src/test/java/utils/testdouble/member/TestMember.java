package utils.testdouble.member;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Builder;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.johoeunsae.exchangediary.member.domain.OauthInfo;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.johoeunsae.exchangediary.member.domain.PasswordInfo;
import org.johoeunsae.exchangediary.member.domain.PasswordMember;
import org.johoeunsae.exchangediary.member.domain.SocialMember;
import org.springframework.security.crypto.password.PasswordEncoder;
import utils.testdouble.TestEntity;

@Builder
public class TestMember implements TestEntity<Member, Long> {

	public static final String DEFAULT_NICKNAME = "nickName";
	public static final String DEFAULT_EMAIL = "test@test.com";
	public static final String DEFAULT_PASSWORD = "password";
	public static final String DEFAULT_PROFILE_IMAGE_URL = "profileImageUrl";
	public static final MemberRole DEFAULT_MEMBER_ROLE = MemberRole.USER;
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_OAUTH_ID = "oauthId";
	public static final OauthType DEFAULT_OAUTH_TYPE = OauthType.NAVER;

	@Builder.Default
	private final String nickname = DEFAULT_NICKNAME;
	@Builder.Default
	private final String email = DEFAULT_EMAIL;
	@Builder.Default
	private final String oauthId = DEFAULT_OAUTH_ID;
	@Builder.Default
	private final OauthType oauthType = DEFAULT_OAUTH_TYPE;
	@Builder.Default
	private final String password = DEFAULT_PASSWORD;
	@Builder.Default
	private final String profileImageUrl = DEFAULT_PROFILE_IMAGE_URL;
	@Builder.Default
	private final MemberRole memberRole = DEFAULT_MEMBER_ROLE;
	@Builder.Default
	private final LocalDateTime createdAt = DEFAULT_TIME;

	public static Member asDefaultEntity() {
		return Member.createSocialMember(
				MemberFeatures.of(DEFAULT_EMAIL, DEFAULT_NICKNAME),
				DEFAULT_TIME,
				OauthInfo.of(DEFAULT_OAUTH_ID, DEFAULT_OAUTH_TYPE)
		);
	}

	public static Member asDefaultEntity(String nickname) {
		return Member.createSocialMember(
				MemberFeatures.of(UUID.randomUUID().toString(), nickname),
				DEFAULT_TIME,
				OauthInfo.of(DEFAULT_OAUTH_ID, DEFAULT_OAUTH_TYPE)
		);
	}

	public static Member asSocialMember(String email, String nickname) {
		return Member.createSocialMember(
				MemberFeatures.of(email, nickname),
				DEFAULT_TIME,
				OauthInfo.of(DEFAULT_OAUTH_ID, DEFAULT_OAUTH_TYPE)
		);
	}

	public static Member asSocialMember(OauthType oauthType) {
		return Member.createSocialMember(
				MemberFeatures.of(DEFAULT_EMAIL, DEFAULT_NICKNAME),
				DEFAULT_TIME,
				OauthInfo.of(DEFAULT_OAUTH_ID, oauthType)
		);
	}

	public static Member asPasswordMember(String email, String nickname, String password,
			PasswordEncoder passwordEncoder) {
		return Member.createPasswordMember(
				MemberFeatures.of(email, nickname),
				DEFAULT_TIME,
				PasswordInfo.createWithHash(nickname, password, passwordEncoder)
		);
	}

	@Override
	public Member asEntity() {
		return Member.of(
				MemberFeatures.of(this.email, this.nickname),
				memberRole,
				createdAt
		);
	}

	@Deprecated
	@Override
	public Member asMockEntity(Long id) {
		throw new UnsupportedOperationException("asSocialMember, asPasswordMemberWith 메서드를 사용하세요.");
	}

	public SocialMember asMockSocialMember(Long id) {
		SocialMember member = mock(SocialMember.class);
		lenient().when(member.getId()).thenReturn(id);
		lenient().when(member.getNickname()).thenReturn(this.nickname);
		lenient().when(member.getEmail()).thenReturn(this.email);
		lenient().when(member.getRole()).thenReturn(this.memberRole);
		lenient().when(member.getCreatedAt()).thenReturn(this.createdAt);
		lenient().when(member.getOauthId()).thenReturn(this.oauthId);
		lenient().when(member.getOauthType()).thenReturn(this.oauthType);

		return member;
	}

	public PasswordMember asMockPasswordMemberWith(Long id, PasswordEncoder passwordEncoder) {
		PasswordMember member = mock(PasswordMember.class);
		lenient().when(member.getId()).thenReturn(id);
		lenient().when(member.getNickname()).thenReturn(this.nickname);
		lenient().when(member.getEmail()).thenReturn(this.email);
		lenient().when(member.getRole()).thenReturn(this.memberRole);
		lenient().when(member.getCreatedAt()).thenReturn(this.createdAt);
		lenient().when(member.getPassword()).thenReturn(this.password);
		return member;
	}

	public SocialMember asSocialMember() {
		return Member.createSocialMember(
				MemberFeatures.of(this.email, this.nickname),
				createdAt,
				OauthInfo.of(this.oauthId, this.oauthType)
		);
	}

	public PasswordMember asPasswordMemberWith(PasswordEncoder passwordEncoder) {
		return Member.createPasswordMember(
				MemberFeatures.of(this.email, this.nickname),
				createdAt,
				PasswordInfo.createWithHash(this.nickname, this.password, passwordEncoder)
		);
	}
}
