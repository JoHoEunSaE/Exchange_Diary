package org.johoeunsae.exchangediary.diary.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.TypedQuery;
import org.johoeunsae.exchangediary.auth.jwt.JwtTokenProvider;
import org.johoeunsae.exchangediary.block.domain.Block;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.diary.domain.CoverColor;
import org.johoeunsae.exchangediary.diary.domain.CoverType;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.dto.DiaryCreateRequestDto;
import org.johoeunsae.exchangediary.dto.DiaryPreviewDto;
import org.johoeunsae.exchangediary.dto.DiaryUpdateRequestDto;
import org.johoeunsae.exchangediary.dto.NoteCreateRequestDto;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.like.domain.Like;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.note.domain.NoteImage;
import org.johoeunsae.exchangediary.note.domain.NoteRead;
import org.johoeunsae.exchangediary.note.domain.VisibleScope;
import org.johoeunsae.exchangediary.utils.obfuscation.DataEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.JsonMatcher;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.diary.TestCoverColor;
import utils.testdouble.diary.TestCoverImage;
import utils.testdouble.diary.TestDiary;
import utils.testdouble.diary.TestRegistration;
import utils.testdouble.member.TestMember;
import utils.testdouble.note.TestNote;

class DiaryControllerTest extends E2EMvcTest {

	private final String URL_PREFIX = "/v1/diaries";
	private final String EMPTY_VALUE = "";
	private final String BEARER = "Bearer ";
	private final String AUTHORIZE_VALUE = "Authorization";
	private final String COVER_DATA = "coverData";
	private final String COVER_TYPE_VALUE = "coverType";
	private final String TITLE_VALUE = "title";
	private final String GROUP_NAME_VALUE = "groupName";
	private final String COVER_TYPE_COLOR = CoverType.COLOR.name();
	private final String COVER_TYPE_IMAGE = CoverType.IMAGE.name();
	private final String normalTitle = "일기장제목";
	private final String strangeTitle = "어디까지올라가는거에요어디까지올라가는거에요어디까지올라가는거에요";
	private final String normalGroupName = "그룹이름";
	private final String strangeGroupName = "블루베리스무디블루베리스무디블루베리스무디블루베리스무디블루베리스무디";
	private final String normalContent = "coverImageDataContent";
	private final String normalCoverColorCode = "#FFFFFF00";
	private final String strangeCoverColorCode = "아기천사김은비졸업추카";
	private final String normalFileExtension = ".png";
	private final String strangeFileExtension = ".대충이상한확장자";
	private PersistHelper persistHelper;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private String token;
	@Value("${spring.images.path.diary-cover}")
	private String DIARY_COVER_IMAGE_DIR;
	private String normalImageUrl;
	@Autowired
	private DataEncoder dataEncoder;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
	}

	private String encodingData(String data) {
		return dataEncoder.encode(data);
	}

	// --------------------------------------- CUD 테스트 ---------------------------------------

	@Nested
	@DisplayName("POST /")
	class CreateDiary {

		private final String url = URL_PREFIX;

		@BeforeEach
		void setUp() {
			Member loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
			normalImageUrl = DIARY_COVER_IMAGE_DIR + "random-string/image" + normalFileExtension;
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 비어있는 색상 코드")
		void 실패_createDiary_비어있는색상코드() throws Exception {
			// given
			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(EMPTY_VALUE)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 유효하지 않은 색상 코드")
		void 실패_createDiary_유효하지않은색상코드() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(strangeCoverColorCode)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 비어있는 커버 이미지")
		void 실패_createDiary_비어있는커버이미지() throws Exception {
			// given
			String content = EMPTY_VALUE;
			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(content)
											.build()
							)
					);
			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 유효하지 않은 커버 이미지 확장자")
		void 실패_createDiary_유효하지않은커버이미지확장자() throws Exception {
			// given
			String content = DIARY_COVER_IMAGE_DIR + "/random-string/image" + strangeFileExtension;
			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(content)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 커버 이미지 파일 크기 초과")
		@Disabled
			// 이미지 관련 변경이 있어 필요없는 테스트 코드
		void 실패_createDiary_커버이미지파일크기초과() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.param(COVER_DATA, EMPTY_VALUE)
					.param(COVER_TYPE_VALUE, COVER_TYPE_IMAGE)
					.param(TITLE_VALUE, normalTitle)
					.param(GROUP_NAME_VALUE, normalGroupName)
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 일기장 제목 길이 초과")
		void 실패_createDiary_일기장제목길이초과() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(normalGroupName)
											.title(strangeTitle)
											.coverData(normalImageUrl)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 생성: 일기장 그룹 이름 길이 초과")
		void 실패_createDiary_일기장그룹이름길이초과() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(strangeGroupName)
											.title(normalTitle)
											.coverData(normalImageUrl)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 일기장 생성: 커버 색상으로 일기장 생성 성공")
		void 성공_createDiary_커버색상으로생성() throws Exception {
			// given
			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(normalCoverColorCode)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("성공 - 일기장 생성: 커버 이미지로 일기장 생성 성공")
		@Disabled
		void 성공_createDiary_커버이미지로생성() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = post(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryCreateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(normalImageUrl)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isCreated());
		}
	}

	@Nested
	@DisplayName("DELETE /{diaryId}")
	class DeleteDiary {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 일기장 삭제: 존재하지 않는 일기장")
		void 실패_존재하지않는일기장() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Long strangeDiaryId = 999999999L;

			Diary diary = TestDiary.builder()
					.masterMember(loginMember)
					.createdAt(now)
					.coverType(CoverType.COLOR)
					.build()
					.asEntity();

			Note note1 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();
			Note note2 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();
			Note note3 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();

			persistHelper
					.persist(diary, note1, note2, note3)
					.flushAndClear();
			// when
			MockHttpServletRequestBuilder requestBuilder = delete(url + "/" + strangeDiaryId)
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isNotFound())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 삭제: 방장이 아닌 멤버 (삭제 권한 없음)")
		void 실패_방장이아닌멤버() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member masterMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("동동그리그리@gmail.com", "동동그리그리"));

			Diary diary = TestDiary.builder()
					.masterMember(masterMember)
					.createdAt(now)
					.coverType(CoverType.COLOR)
					.build()
					.asEntity();

			Note note1 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();
			Note note2 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();
			Note note3 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();

			persistHelper
					.persist(diary, note1, note2, note3)
					.flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isForbidden())
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 일기장 삭제: 일기 없는 일기장 삭제 성공")
		void 성공_일기가없는일기장삭제() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isOk())
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 일기장 삭제: 일기 있는 일기장 삭제 성공")
		void 성공_일기가있는일기장삭제() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = TestDiary.builder()
					.masterMember(loginMember)
					.createdAt(now)
					.coverType(CoverType.COLOR)
					.build()
					.asEntity();

			Note note1 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();
			Note note2 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();
			Note note3 = TestNote.builder().member(loginMember)
					.title(TestNote.DEFAULT_TITLE).content(TestNote.DEFAULT_CONTENT)
					.visibleScope(VisibleScope.PUBLIC)
					.build().asEntity();

			persistHelper
					.persist(diary, note1, note2, note3)
					.flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

//			em에서 diaryId가 diary.getId()와 값이 동일한 일기 조회
			TypedQuery<Note> targetCheckQuery = em.createQuery(
							"select n from Note n where n.diaryId = :diaryId",
							Note.class)
					.setParameter("diaryId", diary.getId());

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isOk())
//					diaryId가 diary.getId()인 일기는 없어야 함.
					.andDo(ignore -> assertThat(targetCheckQuery.getResultList().isEmpty())
							.isTrue())
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("PATCH /{diaryId}")
	class UpdateDiary {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
			normalImageUrl = DIARY_COVER_IMAGE_DIR + "random-string/image" + normalFileExtension;
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 비어있는 색상 코드")
		void 실패_updateDiary_비어있는색상코드() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(EMPTY_VALUE)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 유효하지 않은 색상 코드")
		void 실패_updateDiary_유효하지않은색상코드() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(strangeCoverColorCode)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 비어있는 커버 이미지")
		void 실패_updateDiary_비어있는커버이미지() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);
			String content = EMPTY_VALUE;

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(content)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 유효하지 않은 커버 이미지 파일 확장자")
		void 실패_updateDiary_유효하지않은커버이미지파일확장자() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);
			String content = DIARY_COVER_IMAGE_DIR + "/random-string/image" + strangeFileExtension;

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.groupName(normalGroupName)
											.title(normalTitle)
											.coverData(content)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 커버 이미지 파일 크기 초과")
		@Disabled
		void 실패_updateDiary_커버이미지파일크기초과() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(HttpMethod.PATCH,
					url + "/" + diary.getId())
					.param(COVER_TYPE_VALUE, COVER_TYPE_IMAGE)
					.param(TITLE_VALUE, normalTitle)
					.param(GROUP_NAME_VALUE, normalGroupName)
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 일기장 제목 길이 초과")
		void 실패_updateDiary_일기장제목길이초과() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.groupName(normalGroupName)
											.title(strangeTitle)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 일기장 그룹 이름 길이 초과")
		void 실패_updateDiary_일기장그룹이름길이초과() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.groupName(strangeGroupName)
											.title(normalTitle)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 방장이 아닌 멤버 (일기장 수정 권한 없음)")
		void 실패_updateDiary_방장이아닌멤버() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member masterMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("나헤장아니다@gmail.com", "나헤장아니다"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(masterMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.coverData(normalImageUrl)
											.groupName(normalGroupName)
											.title(normalTitle)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isForbidden())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장 수정: 존재하지 않는 일기장")
		void 실패_updateDiary_존재하지않는일기장() throws Exception {
			// given
			Long strangeDiaryId = 999999999L;
			LocalDateTime now = LocalDateTime.now();

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.IMAGE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + strangeDiaryId)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.coverData(normalImageUrl)
											.groupName(normalGroupName)
											.title(normalTitle)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("성공 - 일기장 수정: 아무것도 수정하지 않음")
		void 성공_아무것도수정하지않음() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverImage.builder()
							.imageUrl(normalImageUrl)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.coverData(normalImageUrl)
											.groupName(normalGroupName)
											.title(normalTitle)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			TypedQuery<Diary> targetCheckQuery1 = em.createQuery(
					"select d from Diary d", Diary.class);
			List<Diary> diaries = targetCheckQuery1.getResultList();

			resultActions
					.andExpect(status().isOk())
//					diaryId가 coverColor.getId()인 일기장은 변경되지 않음.
					.andDo(ignore -> Assertions.assertAll(
									() -> assertThat(diaries.get(0).getTitle().equals(normalTitle))
											.isTrue(),
									() -> assertThat(diaries.get(0).getGroupName().equals(normalGroupName))
											.isTrue(),
									() -> assertThat(diaries.get(0).getCoverImage().getImageUrl()
											.equals(normalImageUrl))
											.isTrue()
							)
					)
					.andDo(print());
		}


		@Test
		@DisplayName("성공 - 일기장 수정: 제목만 변경 성공")
		void 성공_제목만변경() throws Exception {
			// given
			String newTitle = "새로운 일기장 제목";
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverImage.builder()
							.imageUrl(normalImageUrl)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.coverData(normalImageUrl)
											.title(newTitle)
											.groupName(normalGroupName)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isOk())
					.andExpect(response.get(DiaryPreviewDto.Fields.title).isEquals(newTitle))
					.andExpect(response.get(DiaryPreviewDto.Fields.groupName)
							.isEquals(normalGroupName))
					.andExpect(response.get(DiaryPreviewDto.Fields.coverType)
							.isEquals(CoverType.IMAGE.name()))
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 일기장 수정: 그룹 이름만 변경 성공")
		void 성공_그룹이름만변경() throws Exception {
			// given
			String newGroupName = "새로운 일기장 그룹 이름";
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverImage.builder()
							.imageUrl(normalImageUrl)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.groupName(newGroupName)
											.title(normalTitle)
											.coverType(CoverType.IMAGE)
											.coverData(normalImageUrl)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isOk())
					.andExpect(response.get(DiaryPreviewDto.Fields.title)
							.isEquals(normalTitle))
					.andExpect(response.get(DiaryPreviewDto.Fields.groupName)
							.isEquals(newGroupName))
					.andExpect(response.get(DiaryPreviewDto.Fields.coverType)
							.isEquals(CoverType.IMAGE.name()))
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 일기장 수정: 커버 이미지에서 커버 색상으로 변경 성공")
		void 성공_커버이미지에서_커버색상으로_변경() throws Exception {
			String newCoverColorCode = "#000000FF";
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverImage.builder()
							.imageUrl(normalImageUrl)
							.diary(diary)
							.build()
							.asEntity()
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.coverData(newCoverColorCode)
											.title(normalTitle)
											.groupName(normalGroupName)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);
			em.flush();
			em.clear();

			JsonMatcher response = JsonMatcher.create();
			TypedQuery<Diary> targetCheckQuery1 = em.createQuery(
					"select d from Diary d", Diary.class);
			List<Diary> diaries = targetCheckQuery1.getResultList();
			resultActions
					.andExpect(status().isOk())
					.andExpect(response.get(DiaryPreviewDto.Fields.title)
							.isEquals(normalTitle))
					.andExpect(response.get(DiaryPreviewDto.Fields.groupName)
							.isEquals(normalGroupName))
					.andExpect(response.get(DiaryPreviewDto.Fields.coverType)
							.isEquals(CoverType.COLOR.name()))
					.andDo(ignore -> Assertions.assertAll(
									() -> assertThat(diaries.get(0).getCoverImage()).isNull(),
									() -> assertThat(diaries.get(0).getCoverColor().getColorCode()
											.equals(normalCoverColorCode))
											.isFalse(),
									() -> assertThat(diaries.get(0).getCoverColor().getColorCode()
											.equals(newCoverColorCode))
											.isTrue()
							)
					)
					.andDo(print());
		}

		// TODO: s3에 존재하지 않는 이미지이므로 404 반환. s3와 상관없이 테스트 하는 방법 찾아보기
		@Test
		@DisplayName("성공 - 일기장 수정: 커버 이미지에서 커버 이미지로 변경 성공")
		@Disabled
		void 성공_커버이미지에서_커버이미지로_변경() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			String newImageUrl = DIARY_COVER_IMAGE_DIR + "random-string/new" + normalFileExtension;

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverImage.builder()
							.imageUrl(normalImageUrl)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.coverData(newImageUrl)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);
			em.flush();
			em.clear();

			JsonMatcher response = JsonMatcher.create();
			TypedQuery<Diary> targetCheckQuery1 = em.createQuery(
					"select d from Diary d", Diary.class);
			List<Diary> diaries = targetCheckQuery1.getResultList();

			resultActions
					.andExpect(status().isOk())
					.andExpect(response.get(DiaryPreviewDto.Fields.title)
							.isEquals(normalTitle))
					.andExpect(response.get(DiaryPreviewDto.Fields.groupName)
							.isEquals(normalGroupName))
					.andExpect(response.get(DiaryPreviewDto.Fields.coverType)
							.isEquals(CoverType.IMAGE.name()))
					.andDo(ignore -> Assertions.assertAll(
									() -> assertThat(diaries.get(0).getCoverImage().getImageUrl()
											.equals(normalImageUrl))
											.isFalse(),
									() -> assertThat(diaries.get(0).getCoverImage().getImageUrl()
											.equals(newImageUrl))
											.isTrue(),
									() -> assertThat(diaries.get(0).getCoverColor()).isNull()
							)
					)
					.andDo(print());
		}

		// TODO: s3에 존재하지 않는 이미지이므로 404 반환, s3와 상관없이 테스트 하는 방법 찾아보기
		@Test
		@DisplayName("성공 - 일기장 수정: 커버 색상에서 커버 이미지로 변경 성공")
		@Disabled
		void 성공_커버색상에서_커버이미지로_변경() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverColor.builder()
							.colorCode(normalCoverColorCode)
							.diary(diary)
							.build()
							.asEntity()
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.IMAGE)
											.coverData(normalImageUrl)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);
			em.flush();
			em.clear();

			JsonMatcher response = JsonMatcher.create();
			TypedQuery<Diary> targetCheckQuery1 = em.createQuery(
					"select d from Diary d", Diary.class);
			List<Diary> diaries = targetCheckQuery1.getResultList();

			resultActions
					.andExpect(status().isOk())
					.andExpect(response.get(DiaryPreviewDto.Fields.title)
							.isEquals(normalTitle))
					.andExpect(response.get(DiaryPreviewDto.Fields.groupName)
							.isEquals(normalGroupName))
					.andExpect(response.get(DiaryPreviewDto.Fields.coverType)
							.isEquals(CoverType.IMAGE.name()))
					.andDo(ignore -> Assertions.assertAll(
									() -> assertThat(diaries.get(0).getTitle().equals(normalTitle))
											.isTrue(),
									() -> assertThat(diaries.get(0).getGroupName().equals(normalGroupName))
											.isTrue(),
									() -> assertThat(diaries.get(0).getCoverImage().getImageUrl()
											.equals(normalImageUrl))
											.isTrue(),
									() -> assertThat(diaries.get(0).getCoverColor()).isNull()
							)
					)
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 일기장 수정: 커버 색상에서 커버 색상으로 변경 성공")
		void 성공_커버색상에서_커버색상으로_변경() throws Exception {
			// given
			String newCoverColorCode = "#000000FF";
			LocalDateTime now = LocalDateTime.now();

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.title(normalTitle)
							.groupName(normalGroupName)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestCoverColor.builder()
							.colorCode(normalCoverColorCode)
							.diary(diary)
							.build()
							.asEntity()
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(url + "/" + diary.getId())
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									DiaryUpdateRequestDto.builder()
											.coverType(CoverType.COLOR)
											.coverData(newCoverColorCode)
											.title(normalTitle)
											.groupName(normalGroupName)
											.build()
							)
					);

			// then
			ResultActions resultActions = mockMvc.perform(requestBuilder);
			em.flush();
			em.clear();

			JsonMatcher response = JsonMatcher.create();
			TypedQuery<Diary> targetCheckQuery1 = em.createQuery(
					"select d from Diary d", Diary.class);
			List<Diary> diaries = targetCheckQuery1.getResultList();

			resultActions
					.andExpect(status().isOk())
					.andExpect(response.get(DiaryPreviewDto.Fields.title)
							.isEquals(normalTitle))
					.andExpect(response.get(DiaryPreviewDto.Fields.groupName)
							.isEquals(normalGroupName))
					.andExpect(response.get(DiaryPreviewDto.Fields.coverType)
							.isEquals(CoverType.COLOR.name()))
//					diaryId가 coverColor.getId()인 일기장은 커버가 이미지로 변경됨.
					.andDo(ignore -> Assertions.assertAll(
									() -> assertThat(diaries.get(0).getTitle().equals(normalTitle))
											.isTrue(),
									() -> assertThat(diaries.get(0).getGroupName().equals(normalGroupName))
											.isTrue(),
									() -> assertThat(diaries.get(0).getCoverColor().getColorCode()
											.equals(newCoverColorCode))
											.isTrue(),
									() -> assertThat(diaries.get(0).getCoverImage()).isNull()
							)
					)
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("POST /{diaryId}/notes")
	class CreateNoteToDiary {

		private static final String TITLE_VALUE = "title";
		private static final String CONTENT_VALUE = "content";
		private static final String NOTE_IMAGE_DATA_VALUE = "imageData";
		private static final String VISIBLE_SCOPE_VALUE = "visibleScope";
		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
			normalImageUrl = DIARY_COVER_IMAGE_DIR + "random-string/image" + normalFileExtension;
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 비어있는 일기 제목")
		void 실패_createNoteToDiary_비어있는일기제목() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 제목 길이 초과 (공백)")
		void 실패_createNoteToDiary_제목_길이_초과_공백() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);
			String content = "                                                                ";

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(content)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 제목 길이 초과 (최대 길이 초과)")
		void 실패_createNoteToDiary_제목_길이_초과_최대_길이_초과() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.MAX_LENGTH_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 비어있는 일기 내용")
		void 실패_createNoteToDiary_비어있는일기내용() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 내용 공백")
		void 실패_createNoteToDiary_내용_공백() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(EMPTY_VALUE)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 내용 길이 초과 (최대 길이 초과)")
		void 실패_createNoteToDiary_내용_길이_초과_최대_길이_초과() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.MAX_LENGTH_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andExpect(status().isBadRequest())
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 유효하지 않은 일기 이미지 파일 확장자")
		void 실패_createNoteToDiary_유효하지않은일기이미지파일확장자() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			String imageUrl = DIARY_COVER_IMAGE_DIR + "random-string/image" + strangeFileExtension;

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.imageUrls(List.of(imageUrl))
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 유효하지 않은 일기 이미지 파일 확장자 [허용되는 확장자, 허용되지 않는 확장자]")
		void 실패_createNoteToDiary_유효하지않은일기이미지파일확장자_2() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			String strangeNoteImageUrl =
					DIARY_COVER_IMAGE_DIR + "random-string/image" + strangeFileExtension;

			// when

			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.imageUrls(List.of(normalImageUrl, strangeNoteImageUrl))
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 일기 이미지 파일 크기 초과")
		@Disabled
		void 실패_createNoteToDiary_일기_이미지_파일_크기_초과() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);

			MockMultipartFile noteImageDataFile = new MockMultipartFile(NOTE_IMAGE_DATA_VALUE,
					"filename" + normalFileExtension,
					"image/png", new byte[TestNote.MAX_IMAGE_SIZE]);
			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(
					url + "/" + diary.getId() + "/notes")
					.file(noteImageDataFile)
					.param(TITLE_VALUE, TestNote.DEFAULT_TITLE)
					.param(CONTENT_VALUE, TestNote.DEFAULT_CONTENT)
					.param(VISIBLE_SCOPE_VALUE, VisibleScope.PUBLIC.name())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 일기장 멤버가 아닌 멤버 (일기장 일기 생성 권한 없음)")
		void 실패_createNoteToDiary_일기장_멤버가_아닌_멤버() throws Exception {
			LocalDateTime now = LocalDateTime.now();
			Member masterMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("테스트코드짜기@gmail.com", "너무재밌닿ㅎ"));
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(masterMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);
			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장에 일기 생성: 존재하지 않는 일기장")
		void 실패_createNoteToDiary_존재하지않는일기장() throws Exception {
			// given
			Long strangeDiaryId = 999999999L;
			LocalDateTime now = LocalDateTime.now();

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + strangeDiaryId + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("성공 - 일기장에 일기 생성: 제목과 내용만 입력")
		void 성공_createNoteToDiary_제목과_내용만_입력() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);
			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("성공 - 일기장에 일기 생성: 비공개 일기 생성")
		void 성공_createNoteToDiary_비공개_일기_생성() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);
			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PRIVATE)
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						TypedQuery<Note> targetCheckQuery = em.createQuery(
								"select n from Note n where n.diaryId = :diaryId",
								Note.class);
						targetCheckQuery.setParameter("diaryId", diary.getId());
						List<Note> notes = targetCheckQuery.getResultList();

						Assertions.assertAll(
								() -> assertThat(notes.get(0).getVisibleScope())
										.isEqualTo(VisibleScope.PRIVATE)
						);
					})
					.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("성공 - 일기장에 일기 생성: 제목과 내용과 이미지 하나 입력")
		@Disabled
			// 실제로 있는 이미지가 아니면 실패하므로 테스트를 위해 주석처리
		void 성공_createNoteToDiary_제목과_내용과_이미지_하나_입력() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);
			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.imageUrls(List.of(normalImageUrl))
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("성공 - 일기장에 일기 생성: 제목과 내용과 이미지 여러개 입력")
		@Disabled
			// 실제로 있는 이미지가 아니면 실패하므로 테스트를 위해 주석처리
		void 성공_createNoteToDiary_제목과_내용과_이미지_여러개_입력() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.coverType(CoverType.IMAGE)
							.createdAt(now)
							.build()
							.asEntity()
			);
			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + diary.getId() + "/notes")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							objectMapper.writeValueAsString(
									NoteCreateRequestDto.builder()
											.title(TestNote.DEFAULT_TITLE)
											.content(TestNote.DEFAULT_CONTENT)
											.visibleScope(VisibleScope.PUBLIC)
											.imageUrls(List.of(normalImageUrl))
											.build()
							)
					);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						TypedQuery<NoteImage> targetCheckQuery = em.createQuery(
								"select ni from NoteImage ni where ni.note.diaryId = :diaryId",
								NoteImage.class);
						targetCheckQuery.setParameter("diaryId", diary.getId());
						List<NoteImage> noteImages = targetCheckQuery.getResultList();

						Assertions.assertAll(
								() -> assertThat(noteImages.size()).isEqualTo(2)
						);
					})
					.andExpect(status().isCreated());
		}
	}

	@Nested
	@DisplayName("PATCH /{diaryId}/notes/{noteId}")
	class TearOffNoteFromDiary {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 일기장에서 일기 뜯기: 존재하지 않는 일기장")
		void 실패_tearOffNoteFromDiary_존재하지_않는_일기장() throws Exception {
			// given
			Long strangeDiaryId = 999999999L;
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary.getId())
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + strangeDiaryId + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 일기장에서 일기 뜯기: 권한이 없는 일기 (작성자가 아님)")
		void 실패_tearOffNoteFromDiary_작성자가_아님() throws Exception {
			// given
			Member author = persistHelper.persistAndReturn(
					TestMember.asSocialMember("일기작성자@gmail.com", "블루투스동글이"));
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(author)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(author)
							.diaryId(diary.getId())
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장에서 일기 뜯기: 권한이 없는 일기 (일기장에 속하지 않은 일기)")
		void 실패_tearOffNoteFromDiary_일기장에_없는_일기() throws Exception {
			// given
			Member author = persistHelper.persistAndReturn(
					TestMember.asSocialMember("일기작성자@gmail.com", "블루투스동글이"));
			LocalDateTime now = LocalDateTime.now();
			Diary belongedDiary = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			Diary nonBelongedDiary = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(author)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(belongedDiary.getId())
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + nonBelongedDiary.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장에서 일기 뜯기: 존재하지 않는 일기")
		void 실패_tearOffNoteFromDiary_존재하지_않는_일기() throws Exception {
			// given
			Long strangeNoteId = 999999999L;
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/notes/" + strangeNoteId)
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("성공 - 일기장에서 일기 뜯기")
		void 성공_tearOffNoteFromDiary() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);
			Note note1 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary.getId())
							.build()
							.asEntity()
			);
			Note note2 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary.getId())
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/notes/" + note1.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						List<Note> queriedNote1 = em.createQuery(
										"select n from Note n where n.id = :noteId",
										Note.class)
								.setParameter("noteId", note1.getId())
								.getResultList();
						List<Note> queriedNote2 = em.createQuery(
										"select n from Note n where n.id = :noteId",
										Note.class)
								.setParameter("noteId", note2.getId())
								.getResultList();

						Assertions.assertAll(
								() -> assertThat(
										queriedNote1.get(0).getDiaryId()
												.equals(Diary.DEFAULT_DIARY_ID)),
								() -> assertThat(
										queriedNote2.get(0).getDiaryId()
												.equals(diary.getId()))
						);
					})
					.andExpect(status().isOk());
		}
	}

	@Nested
	@DisplayName("PATCH /{diaryId}/master/members/{memberId}")
	class changeDiaryMaster {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 일기장 마스터 변경: 존재하지 않는 일기장")
		void 실패_changeDiaryMaster_존재하지_않는_일기장() throws Exception {
			// given
			Long strangeDiaryId = 999999999L;
			LocalDateTime now = LocalDateTime.now();

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장")
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + strangeDiaryId + "/master/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 일기장 마스터 변경: 마스터 변경 권한 없음 (방장이 아님)")
		void 실패_changeDiaryMaster_마스터_변경_권한_없음() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member masterMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("나는방장이다@gmail.com", "방장이다"));
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(masterMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.diary(diary)
							.member(loginMember)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/master/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장 마스터 변경: 일기장에 속하지 않은 멤버")
		void 실패_changeDiaryMaster_일기장에_없는_멤버() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/master/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장 마스터 변경: 대상 멤버가 이미 마스터")
		void 실패_changeDiaryMaster_대상_멤버가_이미_마스터() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(targetMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/master/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("성공 - 일기장 마스터 변경: 정상적으로 변경 성공")
		void 성공_changeDiaryMaster() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.diary(diary)
							.member(targetMember)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = patch(
					url + "/" + diary.getId() + "/master/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						TypedQuery<Diary> targetCheckQuery = em.createQuery(
								"select d from Diary d where d.id = :diaryId",
								Diary.class);
						targetCheckQuery.setParameter("diaryId", diary.getId());
						List<Diary> diaries = targetCheckQuery.getResultList();
						Assertions.assertAll(
								() -> assertThat(diaries.get(0).getMasterMember())
										.isEqualTo(targetMember)
						);
					})
					.andExpect(status().isOk());
		}
	}

	@Nested
	@DisplayName("DELETE /{diaryId}/members/me")
	class leaveDiary {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 일기장 탈퇴: 존재하지 않는 일기장")
		void 실패_leaveDiary_존재하지_않는_일기장() throws Exception {
			// given
			Long strangeDiaryId = 999999999L;
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + strangeDiaryId + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 일기장 탈퇴: 일기장 멤버가 아닌 멤버")
		void 실패_leaveDiary_일기장_멤버가_아님() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member masterMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("나는방장이다@gmail.com", "방장이다"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(masterMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("성공 - 일기장 탈퇴: 정상적으로 탈퇴 성공 (방장이 아닌 경우)")
		void 성공_leaveDiary_no_master() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member masterMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("나는방장이다@gmail.com", "방장이다"));
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(masterMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.diary(diary)
							.member(loginMember)
							.build()
							.asEntity()
			);
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.diaryId(diary.getId())
							.member(loginMember)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						List<Registration> registrations = em.createQuery(
										"select r from Registration r where r.diary.id = :diaryId and r.member.id = :memberId",
										Registration.class)
								.setParameter("diaryId", diary.getId())
								.setParameter("memberId", loginMember.getId())
								.getResultList();

						List<Note> notes = em.createQuery("select n from Note n", Note.class)
								.getResultList();

						Assertions.assertAll(
								() -> assertThat(registrations.size()).isEqualTo(0),
								() -> assertThat(notes.get(0).getDiaryId()).isEqualTo(
										Diary.DEFAULT_DIARY_ID)
						);
					})
					.andExpect(status().isOk());
		}

		@Test
		@DisplayName("성공 - 일기장 탈퇴: 정상적으로 탈퇴 성공 (방장인데, 일기장에 멤버가 혼자인 경우)")
		void 성공_leaveDiary_yes_master_alone() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.diary(diary)
							.member(loginMember)
							.build()
							.asEntity()
			);
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.diaryId(diary.getId())
							.member(loginMember)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
//			일기장은 삭제되고, 일기장에 속한 자신의 일기들은 모두 기본 일기장으로 이동
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						List<Diary> diaries = em.createQuery(
										"select d from Diary d where d.id = :diaryId",
										Diary.class)
								.setParameter("diaryId", diary.getId())
								.getResultList();

						List<Note> notes = em.createQuery("select n from Note n", Note.class)
								.getResultList();

						Assertions.assertAll(
								() -> assertThat(diaries.size()).isEqualTo(0),
								() -> assertThat(notes.get(0).getDiaryId()).isEqualTo(
										Diary.DEFAULT_DIARY_ID)
						);
					})
					.andExpect(status().isOk());
		}

		@Test
		@DisplayName("성공 - 일기장 탈퇴: 정상적으로 탈퇴 성공 (방장인데, 일기장에 멤버가 혼자가 아닌 경우)")
		void 성공_leaveDiary_yes_master_not_alone() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			persistHelper.persistAndReturn(
					TestRegistration.builder()
							.diary(diary)
							.member(loginMember)
							.build()
							.asEntity(),
					TestRegistration.builder()
							.diary(diary)
							.member(targetMember)
							.build()
							.asEntity()
			);
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.diaryId(diary.getId())
							.member(loginMember)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
//			일기장에 속한 자신의 일기들은 모두 기본 일기장으로 이동하고, 방장은 targetMember로 변경
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						List<Registration> registrations = em.createQuery(
										"select r from Registration r where r.diary.id = :diaryId and r.member.id = :memberId",
										Registration.class)
								.setParameter("diaryId", diary.getId())
								.setParameter("memberId", loginMember.getId())
								.getResultList();

						List<Note> notes = em.createQuery("select n from Note n", Note.class)
								.getResultList();

						List<Diary> diaries = em.createQuery(
										"select d from Diary d where d.id = :diaryId",
										Diary.class)
								.setParameter("diaryId", diary.getId())
								.getResultList();

						Assertions.assertAll(
								() -> assertThat(registrations.size()).isEqualTo(0),
								() -> assertThat(notes.get(0).getDiaryId()).isEqualTo(
										Diary.DEFAULT_DIARY_ID),
								() -> assertThat(diaries.get(0).getMasterMember()).isEqualTo(
										targetMember)
						);
					})
					.andExpect(status().isOk());
		}
	}

	@Nested
	@DisplayName("DELETE /{diaryId}/members/{memberId}")
	class kickMember {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 일기장 멤버 강퇴: 존재하지 않는 일기장")
		void 실패_kickMember_존재하지_않는_일기장() throws Exception {
			// given
			Long strangeDiaryId = 999999999L;
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + strangeDiaryId + "/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 일기장 멤버 강퇴: 존재하지 않는 멤버")
		void 실패_kickMember_존재하지_않는_멤버() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);
			Long strangeMemberId = 999999999L;

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/" + strangeMemberId)
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 일기장 멤버 강퇴: 일기장 마스터가 아닌 멤버")
		void 실패_kickMember_일기장_마스터가_아닌_멤버() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(targetMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장 멤버 강퇴: 일기장에 속하지 않은 멤버")
		void 실패_kickMember_일기장에_속하지_않은_멤버() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장 멤버 강퇴: 방장이 자신을 강퇴")
		void 실패_kickMember_방장이_자신을_강퇴() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/" + loginMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("성공 - 일기장 멤버 강퇴: 정상적으로 강퇴 성공")
		void 성공_kickMember() throws Exception {
			// given
			LocalDateTime now = LocalDateTime.now();
			Member targetMember = persistHelper.persistAndReturn(
					TestMember.asSocialMember("방장이될몸@gmail.com", "미래의 방장"));

			Diary diary = persistHelper.persistAndReturn(
					TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity()
			);
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.diaryId(diary.getId())
							.member(targetMember)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestRegistration.builder()
							.diary(diary)
							.member(targetMember)
							.build()
							.asEntity()
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(
					url + "/" + diary.getId() + "/members/" + targetMember.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andDo(ignore -> {
						List<Registration> registrations = em.createQuery(
										"select r from Registration r where r.diary.id = :diaryId and r.member.id = :memberId",
										Registration.class)
								.setParameter("diaryId", diary.getId())
								.setParameter("memberId", targetMember.getId())
								.getResultList();

						List<Note> notes = em.createQuery("select n from Note n", Note.class)
								.getResultList();

						Assertions.assertAll(
								() -> assertThat(registrations.size()).isEqualTo(0),
								() -> assertThat(notes.get(0).getDiaryId()).isEqualTo(
										Diary.DEFAULT_DIARY_ID)
						);
					})
					.andExpect(status().isOk());
		}
	}

	// --------------------------------------- READ 테스트 ---------------------------------------

	@Nested
	@DisplayName("GET /{diaryId}/members/me")
	class getMyDiary {

		private final String url = URL_PREFIX;
		private final LocalDateTime now = LocalDateTime.now();
		private Member loginMember;
		private Diary diary1;
		private Member diaryMaster;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());

			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();

			diaryMaster = persistHelper.persistAndReturn(
					TestMember.builder()
							.email("master@diary.com")
							.nickname("master")
							.build().asEntity()
			);

			diary1 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(diaryMaster)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);
			CoverColor colorCover = persistHelper.persistAndReturn(
					TestCoverColor.builder()
							.diary(diary1)
							.build()
							.asEntity()
			);
			diary1.setCoverColor(colorCover);
		}

		@Test
		@DisplayName("성공 - 내가 속한 특정 일기장 조회")
		void 성공_getMyDiary() throws Exception {
			// given
			persistHelper.persist(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary1)
							.build()
							.asEntity()
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary1.getId() + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("diaryId").value(diary1.getId()))
					.andExpect(jsonPath("title").value(diary1.getTitle()))
					.andExpect(jsonPath("masterMemberId").value(diaryMaster.getId()))
					.andExpect(jsonPath("coverType").value(diary1.getCoverType().name()))
					.andExpect(jsonPath("groupName").value(diary1.getGroupName()));
		}

		@Test
		@DisplayName("실패 - 내가 속하지 않은 특정 일기장 조회 시도")
		void 실패_getMyDiary() throws Exception {

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary1.getId() + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("GET /members/me")
	class getMyDiaries {

		private final String url = URL_PREFIX;
		private final LocalDateTime now = LocalDateTime.now();
		private Member loginMember;
		private Member diaryMaster;
		private Diary diary1;
		private Diary diary2;
		private Diary diary3;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());

			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();

			diaryMaster = persistHelper.persistAndReturn(
					TestMember.builder()
							.email("master@diary.com")
							.nickname("master")
							.build().asEntity()
			);

			diary1 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(diaryMaster)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			diary2 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(diaryMaster)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			diary3 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(diaryMaster)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			CoverColor colorCover = persistHelper.persistAndReturn(
					TestCoverColor.builder()
							.diary(diary1)
							.build()
							.asEntity()
			);
			diary1.setCoverColor(colorCover);

			CoverColor colorCover2 = persistHelper.persistAndReturn(
					TestCoverColor.builder()
							.diary(diary2)
							.build()
							.asEntity()
			);
			diary2.setCoverColor(colorCover2);

			CoverColor colorCover3 = persistHelper.persistAndReturn(
					TestCoverColor.builder()
							.diary(diary3)
							.build()
							.asEntity()
			);
		}

		@Test
		@DisplayName("성공 - 내가 속한 일기 목록 조회")
		void 성공_getMyDiary() throws Exception {
			// given
			persistHelper.persist(
					Registration.of(loginMember, diary1, now),
					Registration.of(loginMember, diary2, now),
					Registration.of(diaryMaster, diary3, now)
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/members/me")
					.header(AUTHORIZE_VALUE, BEARER + token);

			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].diaryId").value(diary1.getId()))
					.andExpect(jsonPath("$[0].title").value(diary1.getTitle()))
					.andExpect(jsonPath("$[0].groupName").value(diary1.getGroupName()))
					.andExpect(jsonPath("$.length()").value(2));
		}
	}

	@Nested
	@DisplayName("GET /{diaryId}/notes/{noteId}")
	class getNote {

		private final String url = URL_PREFIX;
		private final LocalDateTime now = LocalDateTime.now();
		private Member loginMember;
		private Member member2;
		private Member member3;
		private Diary diary1;
		private Diary diary2;

		// diary1에는 loginMember, member2
		// diary2에는 loginMember, member3
		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());

			member2 = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("member2")
							.email("member2@eee.com")
							.build().asEntity());

			member3 = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("member3")
							.email("member3@eee.com")
							.build()
							.asEntity());

			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();

			diary1 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			diary2 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					Registration.of(loginMember, diary1, now),
					Registration.of(loginMember, diary2, now),
					Registration.of(member2, diary1, now),
					Registration.of(member3, diary2, now)
			).flushAndClear();
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회")
		void 성공_getNoteFromDiary() throws Exception {
			// given
			Note prevNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.minusDays(1))
							.build()
							.asEntity()
			);

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			Note nextNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(3))
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("author.memberId").value(loginMember.getId()))
					.andExpect(jsonPath("prevNoteId").value(prevNote.getId()))
					.andExpect(jsonPath("nextNoteId").value(nextNote.getId()));
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (이전 일기가 없는 경우)")
		void 성공2_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary1.getId())
							.now(now)
							.build()
							.asEntity()
			);

			Note nextNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member2)
							.diaryId(diary1.getId())
							.now(now.plusDays(2))
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary1.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").value(nextNote.getId()));
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (다음 일기가 없는 경우)")
		void 성공3_getNoteFromDiary() throws Exception {
			// given
			persistHelper.persist(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			Note prevNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(2))
							.build()
							.asEntity()
			);

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(3))
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("prevNoteId").value(prevNote.getId()))
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (다음 일기가 다른 유저의 블라인드인 경우)")
		void 성공4_getNoteFromDiary() throws Exception {
			// given
			Note prevNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(3))
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(4))
							.visibleScope(VisibleScope.PUBLIC_BLIND)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("prevNoteId").value(prevNote.getId()))
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (다음 일기가 다른 유저의 블라인드인 경우)")
		void 성공5_getNoteFromDiary() throws Exception {
			// given
			Note prevNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(3))
							.build()
							.asEntity()
			);

			Note blindNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(4))
							.visibleScope(VisibleScope.PUBLIC_BLIND)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("prevNoteId").value(prevNote.getId()))
					.andExpect(jsonPath("nextNoteId").value(blindNote.getId()));
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (다음 일기가 삭제된 일기인 경우)")
		void 성공6_getNoteFromDiary() throws Exception {
			// given
			Note prevNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(3))
							.build()
							.asEntity()
			);

			// 삭제 일기
			Note temp = TestNote.builder()
					.member(member3)
					.diaryId(diary2.getId())
					.now(now.plusDays(4))
					.build()
					.asEntity();

			persistHelper.persist(temp);

			em.createQuery("update Note n set n.deletedAt = :deletedAt where n.id = :noteId")
					.setParameter("deletedAt", now.plusDays(5))
					.setParameter("noteId", temp.getId())
					.executeUpdate();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("prevNoteId").value(prevNote.getId()))
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (좋아요가 2개인 경우)")
		void 성공7_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			persistHelper.persist(
					Like.of(loginMember, note, now),
					Like.of(member2, note, now)
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("author.memberId").value(loginMember.getId()))
					.andExpect(jsonPath("likeCount").value(2))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (일기 작성자를 차단한 경우)")
		void 성공8_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			persistHelper.persist(
					Block.of(MemberFromTo.of(loginMember, member3), now)
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("author.memberId").value(member3.getId()))
					.andExpect(jsonPath("isBlocked").value(true))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (일기를 북마크한 경우)")
		void 성공9_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			persistHelper.persist(
					Bookmark.of(loginMember, note, now)
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("author.memberId").value(member3.getId()))
					.andExpect(jsonPath("isBookmarked").value(true))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (일기를 좋아요한 경우)")
		void 성공10_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			persistHelper.persist(
					Like.of(loginMember, note, now)
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(note.getVisibleScope().name()))
					.andExpect(jsonPath("author.memberId").value(member3.getId()))
					.andExpect(jsonPath("isLiked").value(true))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (내 일기의 visibleScope가 PUBLIC_BLIND인 경우)")
		void 성공11_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.visibleScope(VisibleScope.PUBLIC_BLIND)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(VisibleScope.PUBLIC_BLIND.name()))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (내 일기의 visibleScope가 PRIVATE_BLIND인 경우)")
		void 성공12_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.visibleScope(VisibleScope.PRIVATE_BLIND)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(VisibleScope.PRIVATE_BLIND.name()))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 특정 일기 조회 (어떤 멤버의 일기가 visibleScope가 PRIVATE이고 같은 일기장에 있는 경우)")
		void 성공13_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.visibleScope(VisibleScope.PRIVATE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("noteId").value(note.getId()))
					.andExpect(jsonPath("visibleScope").value(VisibleScope.PRIVATE.name()))
					.andExpect(jsonPath("prevNoteId").isEmpty())
					.andExpect(jsonPath("nextNoteId").isEmpty());
		}

		@Test
		@DisplayName("실패 - 일기장에 권한이 없는 경우")
		void 실패1_getNoteFromDiary() throws Exception {
			// given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary1.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			Member strangeMember = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("strange")
							.email("strange@exchange.com")
							.build()
							.asEntity());
			String strangeMemberToken = jwtTokenProvider.createCommonAccessToken(
					strangeMember.getId()).getTokenValue();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary1.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + strangeMemberToken);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("실패 - 일기장이 존재하지 않는 경우")
		void 실패2_getNoteFromDiary() throws Exception {
			// given
			Long diaryId = 999L;

			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary1.getId())
							.now(now.plusDays(1))
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diaryId + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 일기가 존재하지 않는 경우")
		void 실패3_getNoteFromDiary() throws Exception {
			// given
			Long noteId = 999L;

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary1.getId() + "/notes/" + noteId)
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 다른 유저의 블라인드 처리된 일기를 조회한 경우")
		void 실패4_getNoteFromDiary() throws Exception {
			// given
			Note blindNote = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(4))
							.visibleScope(VisibleScope.PUBLIC_BLIND)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + blindNote.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 삭제된 일기를 조회한 경우")
		void 실패5_getNoteFromDiary() throws Exception {
			// given
			// 삭제 일기
			Note temp = TestNote.builder()
					.member(member3)
					.diaryId(diary2.getId())
					.now(now.plusDays(4))
					.build()
					.asEntity();

			Note deletedNote = persistHelper.persistAndReturn(temp);

			em.createQuery("update Note n set n.deletedAt = :deletedAt where n.id = :noteId")
					.setParameter("deletedAt", now.plusDays(5))
					.setParameter("noteId", deletedNote.getId())
					.executeUpdate();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + deletedNote.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("실패 - 다른 유저의 PRIVATE 일기를 조회한 경우 (같은 일기장에 존재하지 않음)")
		void 실패6_getNoteFromDiary() throws Exception {
			// given
			String otherMemberToken = jwtTokenProvider.createCommonAccessToken(
					member2.getId()).getTokenValue();
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member3)
							.diaryId(diary2.getId())
							.now(now.plusDays(1))
							.visibleScope(VisibleScope.PRIVATE)
							.build()
							.asEntity()
			);

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/notes/" + note.getId())
					.header(AUTHORIZE_VALUE, BEARER + otherMemberToken);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("GET /{diaryId}/notes")
	class getNotes {

		private final String url = URL_PREFIX;
		private final LocalDateTime now = LocalDateTime.now();
		private Member loginMember;
		private String token;
		private Member otherMember;
		private Diary diary;


		@BeforeEach
		void setup() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createCommonAccessToken(loginMember.getId()).getTokenValue();

			diary = persistHelper
					.persistAndReturn(TestDiary.builder()
							.masterMember(loginMember)
							.createdAt(now)
							.coverType(CoverType.COLOR)
							.build()
							.asEntity());

			otherMember = persistHelper
					.persistAndReturn(
							TestMember.asSocialMember("email@example.com", "nickname"));

			persistHelper.persist(
					Registration.of(loginMember, diary, now),
					Registration.of(otherMember, diary, now)
			).flushAndClear();

			persistHelper
					.persist(
							Note.of(loginMember, diary.getId(), now,
									Board.of(encodingData("title1"), encodingData("content1")),
									VisibleScope.PUBLIC),
							Note.of(loginMember, diary.getId(), now.plusDays(1),
									Board.of(encodingData("title2"), encodingData("content2")),
									VisibleScope.PUBLIC),
							Note.of(loginMember, diary.getId(), now.plusDays(2),
									Board.of(encodingData("title3"), encodingData("content3")),
									VisibleScope.PRIVATE)
					).flushAndClear();
		}

		@Test
		@DisplayName("성공 - 첫 번째 페이지를 조회합니다.")
		void 성공_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			persistHelper.persist(
					Note.of(otherMember, diary.getId(), now.plusDays(3),
							Board.of(encodingData("title4"), encodingData("content4")),
							VisibleScope.PUBLIC),
					Note.of(otherMember, diary.getId(), now.plusDays(4),
							Board.of(encodingData("title5"), encodingData("content5")),
							VisibleScope.PUBLIC)
			).flushAndClear();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "3")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(5))
					.andExpect(jsonPath("$.result[0].title").value("title5"))
					.andExpect(jsonPath("$.result[0].author.memberId").value(otherMember.getId()))
					.andExpect(jsonPath("$.result[0].groupName").value(diary.getGroupName()))
					.andExpect(jsonPath("$.result[0].diaryId").value(diary.getId()))
					.andExpect(jsonPath("$.result[1].preview").value("content4"))
					.andExpect(jsonPath("$.result[2].preview").value("content3"));
		}

		@Test
		@DisplayName("성공 - 두 번째 페이지를 조회합니다.")
		void 성공2_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			persistHelper.persist(
					Note.of(otherMember, diary.getId(), now.plusDays(3),
							Board.of(encodingData("title4"), encodingData("content4")),
							VisibleScope.PUBLIC),
					Note.of(otherMember, diary.getId(), now.plusDays(4),
							Board.of(encodingData("title5"), encodingData("content5")),
							VisibleScope.PUBLIC)
			).flushAndClear();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "1")
					.param("size", "3")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(2))
					.andExpect(jsonPath("$.totalLength").value(5))
					.andExpect(jsonPath("$.result[0].title").value("title2"))
					.andExpect(jsonPath("$.result[0].author.memberId").value(loginMember.getId()))
					.andExpect(jsonPath("$.result[0].groupName").value(diary.getGroupName()))
					.andExpect(jsonPath("$.result[0].diaryId").value(diary.getId()))
					.andExpect(jsonPath("$.result[1].preview").value("content1"));
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 일기 목록을 최신 글순으로 조회합니다.")
		void 성공_getNotePreviewPaginationFromDiaryDESC() throws Exception {
			//given

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.param("sort", Direction.DESC.name())
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.result[2].title").value("title1"))
					.andExpect(jsonPath("$.result[1].preview").value("content2"))
					.andExpect(jsonPath("$.result[0].visibleScope").value("PRIVATE"));
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 일기 목록을 오래된 글순으로 조회합니다.")
		void 성공_getNotePreviewPaginationFromDiaryASC() throws Exception {
			//given

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.param("sort", Direction.ASC.name())
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.result[0].title").value("title1"))
					.andExpect(jsonPath("$.result[1].preview").value("content2"))
					.andExpect(jsonPath("$.result[2].visibleScope").value("PRIVATE"));
		}

		@Test
		@DisplayName("성공 - 특정 일기장의 일기 목록을 오래된 글순으로 조회합니다. (두 번째 페이지)")
		void 성공2_getNotePreviewPaginationFromDiaryASC() throws Exception {
			//given
			persistHelper.persist(
					Note.of(otherMember, diary.getId(), now.plusDays(3),
							Board.of(encodingData("title4"), encodingData("content4")),
							VisibleScope.PUBLIC),
					Note.of(otherMember, diary.getId(), now.plusDays(4),
							Board.of(encodingData("title5"), encodingData("content5")),
							VisibleScope.PUBLIC)
			).flushAndClear();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "1")
					.param("size", "3")
					.param("sort", Direction.ASC.name())
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(2))
					.andExpect(jsonPath("$.totalLength").value(5))
					.andExpect(jsonPath("$.result[0].title").value("title4"))
					.andExpect(jsonPath("$.result[1].preview").value("content5"));
		}

		@Test
		@DisplayName("성공 - 좋아요가 2개인 경우")
		void 성공3_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary.getId())
							.now(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					Like.of(loginMember, note, now),
					Like.of(otherMember, note, now)
			).flushAndClear();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(4))
					.andExpect(jsonPath("$.result[0].likeCount").value(2));
		}

		@Test
		@DisplayName("성공 - 가장 최근 일기를 읽은 경우")
		void 성공4_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			Note note = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary.getId())
							.now(now.plusDays(4))
							.build()
							.asEntity()
			);

			persistHelper.persist(NoteRead.of(loginMember, note, now, 0)).flushAndClear();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(4))
					.andExpect(jsonPath("$.result[0].hasRead").value(true))
					.andExpect(jsonPath("$.result[1].hasRead").value(false));
		}

		@Test
		@DisplayName("성공 - 가장 최근 일기의 작성자를 차단한 경우")
		void 성공5_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			Member blockedMember = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("blocked")
							.email("blocked@email.com")
							.build().asEntity()
			);

			persistHelper.persist(
					Block.of(MemberFromTo.of(loginMember, blockedMember), now),
					TestNote.builder()
							.member(blockedMember)
							.diaryId(diary.getId())
							.now(now.plusDays(4))
							.build()
							.asEntity()
			).flushAndClear();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(4))
					.andExpect(jsonPath("$.totalLength").value(4))
					.andExpect(jsonPath("$.result[0].isBlocked").value(true))
					.andExpect(jsonPath("$.result[1].isBlocked").value(false));
		}

		@Test
		@DisplayName("성공 - 일기를 삭제한 경우")
		void 성공6_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			Note note = persistHelper.persistAndReturn(TestNote.builder()
							.member(loginMember)
							.diaryId(diary.getId())
							.now(now.plusDays(4))
							.build()
							.asEntity());

			em.createQuery("update Note n set n.deletedAt = :deletedAt where n.id = :noteId")
					.setParameter("deletedAt", now.plusDays(5))
					.setParameter("noteId", note.getId())
					.executeUpdate();

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(3));
		}

		@Test
		@DisplayName("성공 - 최근 일기 하나가 블라인드 된 경우 (내 일기)")
		void 성공7_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			Note note = persistHelper.persistAndReturn(TestNote.builder()
					.member(loginMember)
					.diaryId(diary.getId())
					.now(now.plusDays(4))
					.visibleScope(VisibleScope.PUBLIC_BLIND)
					.build()
					.asEntity());

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(4))
					.andExpect(jsonPath("$.totalLength").value(4))
					.andExpect(jsonPath("$.result[0].visibleScope").value(VisibleScope.PUBLIC_BLIND.name()));
		}

		@Test
		@DisplayName("성공 - 최근 일기 하나가 블라인드 된 경우 (남의 일기)")
		void 성공8_getNotePreviewPaginationFromDiary() throws Exception {
			//given
			Note note = persistHelper.persistAndReturn(TestNote.builder()
					.member(otherMember)
					.diaryId(diary.getId())
					.now(now.plusDays(4))
					.visibleScope(VisibleScope.PUBLIC_BLIND)
					.build()
					.asEntity());

			//when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary.getId() + "/notes")
					.param("page", "0")
					.param("size", "10")
					.header(AUTHORIZE_VALUE, BEARER + token);

			//then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.result.length()").value(3))
					.andExpect(jsonPath("$.totalLength").value(3));
		}

	}

	@Nested
	@DisplayName("GET /{diaryId}/members")
	class getDiaryMembers {

		private final String url = URL_PREFIX;
		private final LocalDateTime now = LocalDateTime.now();
		private Member loginMember;
		private Member member1;
		private Member member2;
		private Member member3;
		private Diary diary1;
		private Diary diary2;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());

			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();

			diary1 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			diary2 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			member1 = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("member1")
							.email("member1@exchange.com")
							.build().asEntity()
			);

			member2 = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("member2")
							.email("member2@exchange.com")
							.build().asEntity()
			);

			member3 = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("member3")
							.email("member3@exchange.com")
							.build().asEntity()
			);

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary1)
							.build()
							.asEntity()
			).flushAndClear();

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary2)
							.build()
							.asEntity()
			).flushAndClear();

		}

		@Test
		@DisplayName("성공 - 특정 일기장 멤버 조회 (멤버 한 명인 diary1)")
		void 성공_getDiaryMembers() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary1.getId() + "/members")
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].memberId").value(loginMember.getId()))
					.andExpect(jsonPath("$.length()").value(1));
		}

		@Test
		@DisplayName("성공 - 특정 일기장 멤버 조회 (멤버 네 명인 diary2)")
		void 성공_getDiaryMembers2() throws Exception {
			// given
			persistHelper.persist(
					TestRegistration.builder()
							.member(member1)
							.diary(diary2)
							.build()
							.asEntity()
			).flushAndClear();

			persistHelper.persist(
					TestRegistration.builder()
							.member(member2)
							.diary(diary2)
							.build()
							.asEntity()
			).flushAndClear();

			persistHelper.persist(
					TestRegistration.builder()
							.member(member3)
							.diary(diary2)
							.build()
							.asEntity()
			).flushAndClear();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/" + diary2.getId() + "/members")
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[3].memberId").value(member3.getId()))
					.andExpect(jsonPath("$.length()").value(4));
		}
	}

	@Nested
	@DisplayName("GET /members/me/new-notes")
	class getMyDiariesNewNotes {

		private final String url = URL_PREFIX;
		private final LocalDateTime now = LocalDateTime.now();
		private Member loginMember;
		private Member member2;
		private Diary diary1;
		private Diary diary2;
		private Note note1;
		private Note note2;
		private Note note3;
		private Note note4;
		private Note note5;
		private Note note6;
		private Note note7;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());

			member2 = persistHelper.persistAndReturn(
					TestMember.builder()
							.nickname("member2")
							.email("member2@exchange.com")
							.build().asEntity()
			);

			token = jwtTokenProvider.createCommonAccessToken(
					loginMember.getId()).getTokenValue();

			diary1 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			diary2 = persistHelper.persistAndReturn(
					TestDiary.builder().masterMember(loginMember)
							.coverType(CoverType.COLOR)
							.createdAt(now)
							.build()
							.asEntity()
			);

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary1)
							.build()
							.asEntity()
			).flushAndClear();

			persistHelper.persist(
					TestRegistration.builder()
							.member(loginMember)
							.diary(diary2)
							.build()
							.asEntity()
			).flushAndClear();

			persistHelper.persist(
					TestRegistration.builder()
							.member(member2)
							.diary(diary1)
							.build()
							.asEntity()
			).flushAndClear();

			persistHelper.persist(
					TestRegistration.builder()
							.member(member2)
							.diary(diary2)
							.build()
							.asEntity()
			).flushAndClear();

			note1 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.minusDays(5))
							.build()
							.asEntity()
			);

			note2 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary1.getId())
							.now(now.minusDays(4))
							.build()
							.asEntity()
			);

			// note3은 diary2의 new note
			note3 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary2.getId())
							.now(now.minusDays(3))
							.build()
							.asEntity()
			);

			// note4은 diary1의 new note
			note4 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary1.getId())
							.now(now.minusDays(3))
							.build()
							.asEntity()
			);

			// note5은 diary2의 new note
			note5 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member2)
							.diaryId(diary2.getId())
							.now(now.minusDays(2))
							.visibleScope(VisibleScope.PRIVATE_BLIND)
							.build()
							.asEntity()
			);

			// note6은 diary1의 new note지만 블라인드
			note6 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(member2)
							.diaryId(diary1.getId())
							.visibleScope(VisibleScope.PUBLIC_BLIND)
							.now(now.minusDays(2))
							.build()
							.asEntity()
			);

			note7 = persistHelper.persistAndReturn(
					TestNote.builder()
							.member(loginMember)
							.diaryId(diary1.getId())
							.now(now.minusDays(1))
							.build()
							.asEntity()
			);

		}

		@Test
		@DisplayName("성공 - 최신 일기 목록 조회")
		void 성공_getNoteFromDiary() throws Exception {
			// given

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/members/me/new-notes")
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].diaryId").value(diary1.getId()))
					.andExpect(jsonPath("$[0].noteId").value(note7.getId()))
					.andExpect(jsonPath("$[1].diaryId").value(diary2.getId()))
					.andExpect(jsonPath("$[1].noteId").value(note3.getId()));
		}

		@Test
		@DisplayName("성공 - 일기 삭제 후 최신 일기 조회")
		void 성공2_getNoteFromDiary() throws Exception {
			// given
			em.createQuery("update Note n set n.deletedAt = :deletedAt where n.id = :noteId")
					.setParameter("deletedAt", now)
					.setParameter("noteId", note7.getId())
					.executeUpdate();

			// when
			MockHttpServletRequestBuilder requestBuilder = get(
					url + "/members/me/new-notes")
					.header(AUTHORIZE_VALUE, BEARER + token);
			// then
			mockMvc.perform(requestBuilder)
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].diaryId").value(diary1.getId()))
					.andExpect(jsonPath("$[0].noteId").value(note4.getId()))
					.andExpect(jsonPath("$[1].diaryId").value(diary2.getId()))
					.andExpect(jsonPath("$[1].noteId").value(note3.getId()));
		}
	}
}
