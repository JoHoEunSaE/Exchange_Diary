package org.johoeunsae.exchangediary.note.service;

import org.johoeunsae.exchangediary.block.service.BlockQueryService;
import org.johoeunsae.exchangediary.bookmark.repository.BookmarkRepository;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.keys.DiaryMemberCompositeKey;
import org.johoeunsae.exchangediary.keys.NoteMemberCompositeKey;
import org.johoeunsae.exchangediary.like.repository.LikeRepository;
import org.johoeunsae.exchangediary.mapper.NoteMapper;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteRead;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.note.repository.NoteReadRepository;
import org.johoeunsae.exchangediary.note.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import utils.test.UnitTest;
import utils.testdouble.diary.TestDiary;
import utils.testdouble.diary.TestRegistration;
import utils.testdouble.member.TestMember;
import utils.testdouble.note.TestNote;
import utils.testdouble.note.TestNoteRead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.johoeunsae.exchangediary.exception.status.CommonExceptionStatus.UNAUTHENTICATED;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

public class NoteQueryServiceImplTest extends UnitTest {

	private static final Long IGNORE_ID = 999L;
	private static final NoteMemberCompositeKey IGNORE_COMPOSITE_ID = NoteMemberCompositeKey.of(
			IGNORE_ID, IGNORE_ID);
	@InjectMocks
	NoteQueryServiceImpl noteQueryService;
	@Mock
	private NoteRepository noteRepository;
	@Mock
	private NoteReadRepository noteReadRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private BookmarkRepository bookmarkRepository;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private LikeRepository likeRepository;
	@Mock
	private NoteMapper noteMapper;
	@Mock
	private BlockQueryService blockQueryService;


	private String randomString() {
		return UUID.randomUUID().toString();
	}

	@Nested
	@DisplayName("내가 쓴 일기 프리뷰 목록을 조회할 때,")
	class GetMyNotePreviewsByMemberId {

		private final Note note1 = TestNote.builder().build().asMockEntity(IGNORE_ID);
		private final Note note2 = TestNote.builder().build().asMockEntity(IGNORE_ID);
		private final Page<Note> stubbedNotes = new PageImpl<>(List.of(note1, note2));

		@Test
		@DisplayName("본인이 작성한 일기에 따라 목록을 반환한다.")
		void getMyNotePreviewsByMemberId() throws Exception {
			//given
			given(noteRepository.findAllByMemberId(anyLong(), any(Pageable.class)))
					.willReturn(stubbedNotes);

			//when
			noteQueryService.getMyNotePreviewsByMemberId(IGNORE_ID, PageRequest.of(0, 10));

			//then
			then(noteRepository).should().findAllByMemberId(IGNORE_ID, PageRequest.of(0, 10));
			then(noteMapper).should(times(2)).toMyNotePreviewDto(any(Note.class));
			for (Note note : stubbedNotes) {
				then(noteMapper).should().toMyNotePreviewDto(note);
			}
		}
	}

	@Nested
	@DisplayName("사용자의 입장에서 남의 일기 프리뷰 목록을 조회할 때,")
	class GetMemberNotePreviews {

		private final Long loginUserId = IGNORE_ID;
		private final Long targetId = IGNORE_ID + 1;
		private final List<NoteRead> stubbedNoteReads = List.of(
				TestNoteRead.builder().build().asMockEntity(IGNORE_COMPOSITE_ID));
		private final Note note1 = TestNote.builder().build().asMockEntity(IGNORE_ID);
		private final Note note2 = TestNote.builder().build().asMockEntity(IGNORE_ID);
		private final Page<Note> stubbedNotes = new PageImpl<>(List.of(note1, note2));

		@Test
		@DisplayName("읽었는지 여부를 포함하여 목록을 조회한다.")
		void getMemberNotePreviews() throws Exception {
			//given
			Pageable pageable = PageRequest.of(0, 10);
			given(note1.getThumbnailUrl()).willReturn(randomString());
			given(note1.getPreview()).willReturn(randomString());
			given(note2.getThumbnailUrl()).willReturn(randomString());
			given(note2.getPreview()).willReturn(randomString());
			given(noteReadRepository.findAllByMemberId(loginUserId)).willReturn(stubbedNoteReads);
			given(noteRepository.findVisibleNotesByMemberId(anyLong(),
					anyLong(), any(Pageable.class))).willReturn(stubbedNotes);

			//when
			noteQueryService.getMemberNotePreviews(loginUserId, targetId, pageable);

			//then
			then(noteReadRepository).should().findAllByMemberId(loginUserId);
			then(noteRepository).should()
					.findVisibleNotesByMemberId(eq(loginUserId), eq(targetId), eq(pageable));
			then(noteMapper).should(times(2)).toMemberNotePreviewDto(any(Note.class), anyBoolean());
		}
	}

	@Nested
	@DisplayName("일기를 상세 조회할 때,")
	class GetNoteView {

		private final Long loginUserId = IGNORE_ID;
		private final Long authorId = IGNORE_ID + 1;
		private final Long noteId = IGNORE_ID;
		private final Long diaryId = IGNORE_ID + 100;
		private final Member loginUser = TestMember.builder().build()
				.asMockSocialMember(loginUserId);
		private final Member author = TestMember.builder().build().asMockSocialMember(authorId);

		private final Note authorsPublicNote = TestNote.builder()
				.member(author)
				.visibleScope(VisibleScope.PUBLIC)
				.build().asMockEntity(noteId);
		private final Note authorsPrivateNote = TestNote.builder()
				.member(author)
				.visibleScope(VisibleScope.PRIVATE)
				.build().asMockEntity(noteId);
		private final Diary notesDiary = TestDiary.builder()
				.build().asMockEntity(diaryId);

		private final List<Registration> loginUserRegistrations = List.of(TestRegistration.builder()
				.diary(notesDiary)
				.member(loginUser)
				.build().asMockEntity(DiaryMemberCompositeKey.of(notesDiary.getId(), loginUser.getId()))
		);

		@Test
		@DisplayName("비공개인 경우, 본인의 일기가 아니고, 들어가있는 일기장의 일기가 아니라면 조회할 수 없다.")
		void getNoteView1() throws Exception {
			//given
			given(memberRepository.findById(loginUserId)).willReturn(Optional.of(loginUser));
			given(noteRepository.findVisibleNoteByNoteId(loginUserId, noteId)).willReturn(Optional.of(authorsPrivateNote));
			given(authorsPrivateNote.isPrivate()).willReturn(true);
			given(registrationRepository.findAllByMemberId(loginUserId)).willReturn(List.of());

			//when, then
			assertThatThrownBy(() -> noteQueryService.getNoteView(loginUserId, noteId))
					.isInstanceOf(ServiceException.class)
					.extracting(ServiceException.Fields.errorReason)
					.isEqualTo(UNAUTHENTICATED.getErrorReason());
		}


		@Test
		@DisplayName("비공개인 경우, 본인의 일기가 아니더라도 들어가있는 일기장의 일기라면 조회할 수 있다.")
		void getNoteView() throws Exception {
			//given
			given(memberRepository.findById(loginUserId)).willReturn(Optional.of(loginUser));
			given(noteRepository.findVisibleNoteByNoteId(loginUserId, noteId)).willReturn(Optional.of(authorsPrivateNote));
			given(blockQueryService.isBlocked(loginUserId, authorId)).willReturn(false);
			given(registrationRepository.findAllByMemberId(loginUserId)).willReturn(loginUserRegistrations);

			//when, then
			assertThatNoException().isThrownBy(() -> noteQueryService.getNoteView(loginUserId, noteId));
		}

		@DisplayName("비공개인 경우, 본인의 일기라면 조회할 수 있다.")
		@Test
		void getNoteView2() {
			//given
			given(memberRepository.findById(authorId)).willReturn(Optional.of(author));
			given(noteRepository.findVisibleNoteByNoteId(authorId, noteId)).willReturn(Optional.of(authorsPrivateNote));

			//when
			assertThatNoException().isThrownBy(() -> noteQueryService.getNoteView(authorId, noteId));
		}

		@DisplayName("공개인 경우, 조회할 수 있다.")
		@Test
		void getNoteView3() {
			//given
			given(memberRepository.findById(loginUserId)).willReturn(Optional.of(loginUser));
			given(noteRepository.findVisibleNoteByNoteId(loginUserId, noteId)).willReturn(Optional.of(authorsPublicNote));
			given(blockQueryService.isBlocked(loginUserId, authorId)).willReturn(false);
			//when
			assertThatNoException().isThrownBy(() -> noteQueryService.getNoteView(loginUserId, noteId));
		}

	}

}
