package utils.testdouble.diary;

import lombok.Builder;
import org.johoeunsae.exchangediary.diary.domain.CoverImage;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.member.domain.Member;
import utils.testdouble.TestEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Builder
public class TestCoverImage implements TestEntity<CoverImage, Long> {

	public static final String DEFAULT_IMAGE_URL = "imageUrl";

	@Builder.Default
	private final String imageUrl = DEFAULT_IMAGE_URL;
	@Builder.Default
	private final Diary diary = null;

	public static CoverImage asDefaultEntity(Diary diary) {
		return CoverImage.of(
				diary,
				DEFAULT_IMAGE_URL
		);
	}

	@Override
	public CoverImage asEntity() {
		return CoverImage.of(
				this.diary,
				this.imageUrl
		);
	}

	@Override
	public CoverImage asMockEntity(Long id) {
		CoverImage coverColor = mock(CoverImage.class);
		lenient().when(coverColor.getId()).thenReturn(id);
		lenient().when(coverColor.getImageUrl()).thenReturn(this.imageUrl);
		lenient().when(coverColor.getDiary()).thenReturn(TestDiary.asDefaultEntity());
		return coverColor;
	}
}
