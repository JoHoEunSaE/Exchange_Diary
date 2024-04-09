package utils.testdouble.note;

import lombok.Builder;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import utils.testdouble.TestEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Builder
public class TestNoteImage implements TestEntity<NoteImage, Long> {
	public static final Integer DEFAULT_INDEX = 0;
	public static final String DEFAULT_IMAGE_URL = "imageUrl";

	@Builder.Default
	private Integer index = DEFAULT_INDEX;
	@Builder.Default
	private String imageUrl = DEFAULT_IMAGE_URL;
	@Builder.Default
	private Note note = null;

	public static NoteImage asDefaultEntity(Note note) {
		return TestNoteImage.builder()
				.note(note)
				.build().asEntity();
	}

	public static Collection<NoteImage> createEntitiesOf(Note note, String... imageUrls) {
		AtomicInteger index = new AtomicInteger(0);
		return Arrays.stream(imageUrls).map(
				imageUrl -> TestNoteImage.builder()
						.note(note)
						.imageUrl(imageUrl)
						.index(index.getAndIncrement())
						.build()
						.asEntity()
		).collect(Collectors.toList());
	}

	@Override public NoteImage asEntity() {
		return NoteImage.of(
				this.note,
				this.index,
				this.imageUrl);
	}

	@Override public NoteImage asMockEntity(Long id) {
		NoteImage noteImage = mock(NoteImage.class);
		lenient().when(noteImage.getId()).thenReturn(id);
		lenient().when(noteImage.getNote()).thenReturn(this.note);
		lenient().when(noteImage.getIndex()).thenReturn(this.index);
		lenient().when(noteImage.getImageUrl()).thenReturn(this.imageUrl);
		return noteImage;
	}
}
