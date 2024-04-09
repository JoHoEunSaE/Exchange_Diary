package utils.testdouble.member;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberToken;
import utils.testdouble.TestEntity;
@Builder
public class TestMemberToken {

	public static final String DEFAULT_TOKEN = "refresh_token";
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);

	@Builder.Default
	private String token = DEFAULT_TOKEN;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	private Member member;

	public static MemberToken asDefaultEntity(Member member) {
		return MemberToken.of(member, DEFAULT_TOKEN, DEFAULT_TIME);
	}

	public static MemberToken asEntity(Member member, String token) {
		return MemberToken.of(member, token, DEFAULT_TIME);
	}

	public MemberToken asMockMemberToken(Long id) {
		MemberToken memberToken = mock(MemberToken.class);
		lenient().when(memberToken.getId()).thenReturn(id);
		lenient().when(memberToken.getToken()).thenReturn(this.token);
		lenient().when(memberToken.getMember()).thenReturn(this.member);
		lenient().when(memberToken.getCreatedAt()).thenReturn(this.createdAt);
		return memberToken;
	}
}
