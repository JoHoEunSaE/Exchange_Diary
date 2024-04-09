package utils.testdouble.diary;

import lombok.Builder;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.keys.DiaryMemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import utils.testdouble.TestEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Builder
public class TestRegistration implements TestEntity<Registration, DiaryMemberCompositeKey> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT);

	@Builder.Default
	private Member member = null;
	@Builder.Default
	private Diary diary = null;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;

	public static Collection<Registration> createEntitiesOf(Diary diary, Member... members) {
		return Arrays.stream(members).map(
				member -> TestRegistration.builder()
						.member(member)
						.diary(diary)
						.build()
						.asEntity()
		).collect(Collectors.toList());
	}

	@Override public Registration asEntity() {
		return Registration.of(
				this.member,
				this.diary,
				this.createdAt
		);
	}

	@Override public Registration asMockEntity(DiaryMemberCompositeKey id) {
		Registration registration = mock(Registration.class);
		lenient().when(registration.getId()).thenReturn(id);
		lenient().when(registration.getMember()).thenReturn(this.member);
		lenient().when(registration.getDiary()).thenReturn(this.diary);
		lenient().when(registration.getRegisteredAt()).thenReturn(this.createdAt);
		return registration;
	}
}
