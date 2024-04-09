package org.johoeunsae.exchangediary.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.johoeunsae.exchangediary.dto.AuthorDto;
import org.johoeunsae.exchangediary.dto.DiarySimpleInfoDto;
import org.johoeunsae.exchangediary.dto.MemberNotePreviewDto;
import org.johoeunsae.exchangediary.dto.MyNotePreviewDto;
import org.johoeunsae.exchangediary.dto.NoteImageDto;
import org.johoeunsae.exchangediary.dto.NoteLinkedViewDto;
import org.johoeunsae.exchangediary.dto.NotePreviewDto;
import org.johoeunsae.exchangediary.dto.NoteViewDto;
import org.johoeunsae.exchangediary.image.service.ImageService;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import utils.test.UnitTest;
import utils.testdouble.member.TestMember;
import utils.testdouble.note.TestNote;
import utils.testdouble.note.TestNoteImage;

public class NoteMapperTest extends UnitTest {
	@Mock
	private ImageService imageService;

	@Mock
	private MemberMapper memberMapper;

	@InjectMocks
	private NoteMapper noteMapper = Mappers.getMapper(NoteMapper.class);

	private Note testNote;
	private NoteImage testNoteImage;
	private List<NoteImage> testNoteImages;
	private Member author;

	private static final String MOCKED_IMAGE_URL = "http://image-url/note-images/1.jpg";
	private static final Long MOCKED_NOTE_ID = 1L;
	private static final Long MOCKED_MEMBER_ID = 1L;

	@BeforeEach
	void setUp() {
		// ImageService가 호출될 때 반환할 가짜 URL을 설정합니다.
		when(imageService.getImageUrl(anyString())).thenReturn(MOCKED_IMAGE_URL);

		testNote = TestNote.builder().build().asMockEntity(MOCKED_NOTE_ID);
		testNoteImage = TestNoteImage.asDefaultEntity(testNote);
		testNoteImages = List.of(testNoteImage);
		author = TestMember.builder().build().asMockSocialMember(MOCKED_MEMBER_ID);
	}

	@Test
	public void testToMemberNotePreviewDto() {
		// given
		boolean hasRead = true;
		when(testNote.getThumbnailUrl()).thenReturn("1.jpg");

		// when
		MemberNotePreviewDto result = noteMapper.toMemberNotePreviewDto(testNote, hasRead);

		// then
		assertEquals(testNote.getId(), result.getNoteId());
		assertEquals(testNote.getPreview(), result.getPreview());
		assertEquals(MOCKED_IMAGE_URL, result.getThumbnailUrl());
		assertEquals(hasRead, result.isHasRead());
	}

	@Test
	public void testToMyNotePreviewDto() {
		// given
		boolean hasRead = true;
		when(testNote.getThumbnailUrl()).thenReturn("1.jpg");

		// when
		MyNotePreviewDto result = noteMapper.toMyNotePreviewDto(testNote);

		// then
		assertEquals(testNote.getId(), result.getNoteId());
		assertEquals(testNote.getPreview(), result.getPreview());
		assertEquals(MOCKED_IMAGE_URL, result.getThumbnailUrl());
	}

	@Test
	public void testToNoteLinkedViewDto() {
		// given
		boolean isBookmarked = true;
		boolean isLiked = true;
		Integer likeCount = 10;
		Long nextNoteId = 2L;
		Long prevNoteId = 0L;
		AuthorDto authorDto = new AuthorDto();

		when(testNote.getNoteImages()).thenReturn(testNoteImages);
		when(testNote.getMember()).thenReturn(author);
		when(memberMapper.toAuthorDto(author)).thenReturn(authorDto);

		// when
		NoteLinkedViewDto result = noteMapper.toNoteLinkedViewDto(testNote, isBookmarked,
				isLiked, likeCount, nextNoteId, prevNoteId);

		// then
		assertEquals(testNote.getId(), result.getNoteId());
		assertEquals(MOCKED_IMAGE_URL, result.getImageList().get(0).getImageUrl());
		assertEquals(testNoteImage.getIndex(), result.getImageList().get(0).getImageIndex());
		assertEquals(authorDto, result.getAuthor());
		assertEquals(isBookmarked, result.isBookmarked());
		assertEquals(isLiked, result.isLiked());
		assertEquals(likeCount, result.getLikeCount());
		assertEquals(nextNoteId, result.getNextNoteId());
		assertEquals(prevNoteId, result.getPrevNoteId());
	}

	@Test
	public void testToNotePreviewDto() {
		// given
		boolean hasRead = true;
		boolean isBlocked = true;
		Integer likeCount = 10;
		AuthorDto authorDto = new AuthorDto();
		DiarySimpleInfoDto diary = new DiarySimpleInfoDto();

		when(testNote.getThumbnailUrl()).thenReturn("1.jpg");
		when(testNote.getMember()).thenReturn(author);
		when(memberMapper.toAuthorDto(author)).thenReturn(authorDto);

		// when
		NotePreviewDto result = noteMapper.toNotePreviewDto(testNote, diary, author,
				hasRead, isBlocked, likeCount);

		// then
		assertEquals(testNote.getId(), result.getNoteId());
		assertEquals(testNote.getPreview(), result.getPreview());
		assertEquals(MOCKED_IMAGE_URL, result.getThumbnailUrl());
		assertEquals(authorDto, result.getAuthor());
		assertEquals(likeCount, result.getLikeCount());
		assertEquals(hasRead, result.isHasRead());
		assertEquals(isBlocked, result.isBlocked());
	}

	@Test
	public void testToNoteViewDto() {
		// given
		boolean isBookmarked = true;
		boolean isLiked = true;
		boolean isBlocked = true;
		Integer likeCount = 10;
		AuthorDto authorDto = new AuthorDto();

		when(testNote.getNoteImages()).thenReturn(testNoteImages);
		when(testNote.getMember()).thenReturn(author);
		when(memberMapper.toAuthorDto(author)).thenReturn(authorDto);

		// when
		NoteViewDto result = noteMapper.toNoteViewDto(testNote, isBookmarked,
				isLiked, isBlocked, likeCount);

		// then
		assertEquals(testNote.getId(), result.getNoteId());
		assertEquals(MOCKED_IMAGE_URL, result.getImageList().get(0).getImageUrl());
		assertEquals(testNoteImage.getIndex(), result.getImageList().get(0).getImageIndex());
		assertEquals(authorDto, result.getAuthor());
		assertEquals(isBookmarked, result.isBookmarked());
		assertEquals(isLiked, result.isLiked());
		assertEquals(likeCount, result.getLikeCount());
	}

	@Test
	void toNoteImageDto() {
		// given

		// when
		NoteImageDto result = noteMapper.toNoteImageDto(testNoteImage);

		// then
		assertEquals(testNoteImage.getIndex(), result.getImageIndex());
		assertEquals(MOCKED_IMAGE_URL, result.getImageUrl());
	}

	@Test
	void totoNoteImageDtoList() {
		// given

		// when
		List<NoteImageDto> result = noteMapper.toNoteImageDtoList(testNoteImages);

		// then
		assertEquals(testNoteImage.getIndex(), result.get(0).getImageIndex());
		assertEquals(MOCKED_IMAGE_URL, result.get(0).getImageUrl());
	}
}
