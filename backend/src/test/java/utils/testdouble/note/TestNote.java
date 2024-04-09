package utils.testdouble.note;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.Builder;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import utils.testdouble.TestEntity;

@Builder
public class TestNote implements TestEntity<Note, Long> {

	public static final String DEFAULT_TITLE = "title";
	public static final String MAX_LENGTH_TITLE = String.format("%64s", "a").replace(' ', 'a');
	public static final String DEFAULT_CONTENT = "content";
	public static final String MAX_LENGTH_CONTENT = String.format("%4096s", "a").replace(' ', 'a');
	public static final int MAX_IMAGE_SIZE = 1048576 * 100;
	public static final VisibleScope DEFAULT_VISIBLE_SCOPE = VisibleScope.PUBLIC;
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final Long DEFAULT_DIARY_ID = 999L;
	private static final Base64.Encoder encoder = Base64.getEncoder();


	@Builder.Default
	private final List<NoteImage> noteImages = new ArrayList<>();
	@Builder.Default
	private String title = DEFAULT_TITLE;
	@Builder.Default
	private String content = DEFAULT_CONTENT;
	@Builder.Default
	private VisibleScope visibleScope = DEFAULT_VISIBLE_SCOPE;
	@Builder.Default
	private LocalDateTime now = DEFAULT_TIME;
	@Builder.Default
	private Long diaryId = DEFAULT_DIARY_ID;
	@Builder.Default
	private Member member = null;
	@Builder.Default
	private LocalDateTime deletedAt = null;

	public static Note asDefaultEntity(Member member) {
		return TestNote.builder().member(member).build().asEntity();
	}

	@Override
	public Note asEntity() {
		encodeContent();
		encodeTitle();
		return Note.of(
				this.member,
				this.diaryId,
				this.now,
				Board.of(this.title, this.content),
				this.visibleScope);
	}

	private String encode(String data) {
		return encoder.encodeToString(data.getBytes());
	}

	public void encodeContent() {
		this.content = encode(this.content);
	}

	public void encodeTitle() {
		this.title = encode(this.title);
	}

	@Override
	public Note asMockEntity(Long id) {
		Note note = mock(Note.class);
		lenient().when(note.getId()).thenReturn(id);
		lenient().when(note.getMember()).thenReturn(this.member);
		lenient().when(note.getDiaryId()).thenReturn(this.diaryId);
		lenient().when(note.getCreatedAt()).thenReturn(this.now);
		lenient().when(note.getDiaryId()).thenReturn(this.diaryId);
		lenient().when(note.getContent()).thenReturn(this.content);
		lenient().when(note.getTitle()).thenReturn(this.title);
		lenient().when(note.getVisibleScope()).thenReturn(this.visibleScope);
		lenient().when(note.getDeletedAt()).thenReturn(null);
		return note;
	}
}
