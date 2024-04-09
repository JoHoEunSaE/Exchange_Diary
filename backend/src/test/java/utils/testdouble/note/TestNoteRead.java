package utils.testdouble.note;

import lombok.Builder;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteRead;
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
public class TestNoteRead implements TestEntity<NoteRead, NoteMemberCompositeKey> {
	public static final Integer DEFAULT_COUNTS = 0;
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT);

	@Builder.Default
	private Member member = null;
	@Builder.Default
	private Note note = null;
	@Builder.Default
	private LocalDateTime now = DEFAULT_TIME;
	@Builder.Default
	private Integer counts = DEFAULT_COUNTS;

	public static Collection<NoteRead> createEntitiesOf(Note note, Member... members) {
		return Arrays.stream(members).map(
				member -> TestNoteRead.builder()
						.member(member)
						.note(note)
						.build()
						.asEntity()
		).collect(Collectors.toList());
	}

	@Override public NoteRead asEntity() {
		return NoteRead.of(
				this.member,
				this.note,
				this.now,
				this.counts);
	}

	@Override public NoteRead asMockEntity(NoteMemberCompositeKey id) {
		NoteRead noteRead = mock(NoteRead.class);
		lenient().when(noteRead.getId()).thenReturn(id);
		lenient().when(noteRead.getMember()).thenReturn(this.member);
		lenient().when(noteRead.getNote()).thenReturn(this.note);
		lenient().when(noteRead.getReadAt()).thenReturn(this.now);
		lenient().when(noteRead.getCounts()).thenReturn(this.counts);
		return noteRead;
	}
}
