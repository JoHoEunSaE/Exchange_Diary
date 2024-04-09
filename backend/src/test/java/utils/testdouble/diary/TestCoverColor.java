package utils.testdouble.diary;

import lombok.Builder;
import org.johoeunsae.exchangediary.diary.domain.CoverColor;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import utils.testdouble.TestEntity;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Builder
public class TestCoverColor implements TestEntity<CoverColor, Long> {

	public static final String DEFAULT_COLOR_CODE = "#FFFFFF";

	@Builder.Default
	private final String colorCode = DEFAULT_COLOR_CODE;
	@Builder.Default
	private final Diary diary = null;

	public static CoverColor asDefaultEntity(Diary diary) {
		return CoverColor.of(
				diary,
				DEFAULT_COLOR_CODE
		);
	}

	@Override
	public CoverColor asEntity() {
		return CoverColor.of(
				this.diary,
				this.colorCode
		);
	}

	@Override
	public CoverColor asMockEntity(Long id) {
		CoverColor coverColor = mock(CoverColor.class);
		lenient().when(coverColor.getId()).thenReturn(id);
		lenient().when(coverColor.getColorCode()).thenReturn(this.colorCode);
		lenient().when(coverColor.getDiary()).thenReturn(TestDiary.asDefaultEntity());
		return coverColor;
	}
}
