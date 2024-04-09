package utils.testdouble.follow;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.follow.domain.Follow;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import utils.testdouble.TestEntity;

@Builder
public class TestFollow implements TestEntity<Follow, MemberCompositeKey> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(1, 1, 1, 0, 0);
	@Builder.Default
	private Member from = null;
	@Builder.Default
	private Member to = null;
	@Builder.Default
	private LocalDateTime followedAt = DEFAULT_TIME;

	public static List<Follow> ofMany(Member from, LocalDateTime now, Member... tos) {
		return Stream.of(tos)
				.map(to -> Follow.of(MemberFromTo.of(from, to), now))
				.collect(Collectors.toList());
	}

	@Override
	public Follow asEntity() {
		return Follow.of(MemberFromTo.of(from, to), DEFAULT_TIME);
	}

	@Override
	public Follow asMockEntity(MemberCompositeKey id) {
		Follow follow = mock(Follow.class);
		lenient().when(follow.getId()).thenReturn(id);
		lenient().when(follow.getFrom()).thenReturn(from);
		lenient().when(follow.getTo()).thenReturn(to);
		lenient().when(follow.getCreatedAt()).thenReturn(followedAt);
		return follow;
	}

}
