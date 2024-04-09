package utils.testdouble.diary;

import lombok.Builder;
import org.johoeunsae.exchangediary.diary.domain.CoverColor;
import org.johoeunsae.exchangediary.diary.domain.CoverImage;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.member.domain.Member;
import utils.testdouble.TestEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Builder
public class TestDiary implements TestEntity<Diary, Long> {

	public static final String DEFAULT_TITLE = "title";
	public static final String DEFAULT_GROUP_NAME = "groupName";
	public static final String DEFAULT_COVER_IMAGE_URL = "coverImageUrl";
	public static final String DEFAULT_COVER_COLOR = "#FFFFFF";
	public static final CoverType DEFAULT_COVER_TYPE = CoverType.COLOR;
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);

	@Builder.Default
	private String title = DEFAULT_TITLE;
	@Builder.Default
	private String groupName = DEFAULT_GROUP_NAME;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private Member masterMember = null;
	@Builder.Default
	private CoverType coverType = DEFAULT_COVER_TYPE;

	public static Diary asDefaultEntity() {
		return TestDiary.builder().build().asEntity();
	}

	@Override
	public Diary asEntity() {
		return Diary.of(
				this.masterMember,
				this.createdAt,
				this.title,
				this.groupName,
				this.coverType
		);
	}

	public Diary asEntityWithCoverImage(String coverImageUrl) {
		Diary diary = Diary.of(
				this.masterMember,
				this.createdAt,
				this.title,
				this.groupName,
				CoverType.IMAGE
		);
		CoverImage coverImage = CoverImage.of(
				diary,
				coverImageUrl);
		diary.setCoverImage(coverImage);
		return diary;
	}

	public Diary asEntityWithCoverColor(String coverColor) {
		Diary diary = Diary.of(
				this.masterMember,
				this.createdAt,
				this.title,
				this.groupName,
				CoverType.COLOR
		);
		CoverColor coverColorEntity = CoverColor.of(
				diary,
				coverColor);
		diary.setCoverColor(coverColorEntity);
		return diary;
	}

	@Override
	public Diary asMockEntity(Long id) {
		Diary diary = mock(Diary.class);
		lenient().when(diary.getId()).thenReturn(id);
		lenient().when(diary.getMasterMember()).thenReturn(this.masterMember);
		lenient().when(diary.getTitle()).thenReturn(this.title);
		lenient().when(diary.getCreatedAt()).thenReturn(this.createdAt);
		lenient().when(diary.getGroupName()).thenReturn(this.groupName);
		return diary;
	}
}
