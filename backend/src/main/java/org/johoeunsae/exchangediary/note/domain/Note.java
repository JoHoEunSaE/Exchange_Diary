package org.johoeunsae.exchangediary.note.domain;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;
import org.johoeunsae.exchangediary.dto.entity.Board;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;
import org.johoeunsae.exchangediary.utils.obfuscation.DecodeSerializer;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTE")
@FieldNameConstants
@Entity
public class Note extends IdentityIdDomain implements Validatable {

	private final static int PREVIEW_LENGTH = 30;
	private final static int THUMBNAIL_INDEX = 0;
	private final static int MAX_CONTENT_LENGTH = 4095;
	private final static int MAX_TITLE_LENGTH = 63;

	@ToString.Exclude
	@OneToMany(mappedBy = "note",
			targetEntity = NoteImage.class,
			fetch = FetchType.LAZY)
	private final List<NoteImage> noteImages = new ArrayList<>();

	/* 고유 정보 { */
	@Column(name = "TITLE", length = MAX_TITLE_LENGTH, nullable = false)
	private String title;

	@Column(name = "CONTENT", length = MAX_CONTENT_LENGTH, nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "VISIBLE_SCOPE", nullable = false, length = 20)
	private VisibleScope visibleScope;

	@Column(name = "UPDATED_AT", nullable = false)
	private LocalDateTime updatedAt;


	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "DELETED_AT")
	private LocalDateTime deletedAt;

	@Column(name = "DIARY_ID")
	@ColumnDefault("0")
	private Long diaryId;

	@JoinColumn(name = "MEMBER_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member member;

	/* 생성자 { */
	protected Note(
			Member member, Long diaryId,
			LocalDateTime createdAt, LocalDateTime updatedAt,
			String title, String content,
			VisibleScope visibleScope) {
		this.title = title;
		this.content = content;
		this.visibleScope = visibleScope;
		this.updatedAt = updatedAt;
		this.createdAt = createdAt;
		this.diaryId = diaryId;
		this.member = member;
		this.deletedAt = null;
		RuntimeExceptionThrower.validateDomain(this);
	}

	public static Note of(
			Member member, Long diaryId, LocalDateTime now, Board board,
			VisibleScope visibleScope) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		return new Note(member, diaryId, now, now, board.getTitle(), board.getContent(),
				visibleScope);
	}
	/* } 생성자 */

	public String getPreview() {
		String decodedContent = DecodeSerializer.decodeData(this.content);
		int limitLength = Math.min(decodedContent.length(), PREVIEW_LENGTH);
		return decodedContent.substring(0, limitLength);
	}

	public String getThumbnailUrl() {
		if (this.noteImages.isEmpty()) {
			return ""; // TODO: 빈 문자열에 대한 고려
		}
		return this.noteImages.get(THUMBNAIL_INDEX).getImageUrl();
	}

	@Override
	public boolean isValid() {
		return this.content != null
				&& !this.content.isEmpty()
				&& this.title != null
				&& !this.title.isEmpty()
				&& this.visibleScope != null
				&& this.createdAt != null;
	}

	public void writeContent(String content) {
		this.content = content;
	}

	public void writeTitle(String title) {
		this.title = title;
	}

	public boolean isOwnedBy(Member member) {
		return member.equals(this.member);
	}

	public boolean isPrivate() {
		return this.visibleScope.equals(VisibleScope.PRIVATE);
	}

	public boolean isPublic() {
		return this.visibleScope.equals(VisibleScope.PUBLIC);
	}

	public void addNoteImages(List<NoteImage> noteImages) {
		this.noteImages.addAll(noteImages);
	}

	public void setNoteImages(List<NoteImage> noteImages) {
		this.noteImages.clear();
		this.noteImages.addAll(noteImages);
	}

	public void updateDiaryId(Long diaryId) {
		this.diaryId = diaryId;
	}

	public void updateVisibleScope(VisibleScope visibleScope) {
		this.visibleScope = visibleScope;
	}

	/* 편의 메서드{ */

	public void removeNoteImage(NoteImage noteImage) {
		RuntimeExceptionThrower.checkIdLoaded(noteImage);
		getNoteImages().remove(noteImage);
	}
	/* } 편의 메서드 */
}
