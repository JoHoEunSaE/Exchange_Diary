package org.johoeunsae.exchangediary.note.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;
import org.springframework.http.HttpStatus;

import javax.persistence.*;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTE_IMAGE") @Entity
public class NoteImage extends IdentityIdDomain implements Validatable {

	//TODO : Value로 주입하도록 변경
	private final static String NOTE_IMAGE_DIRECTORY = "note-images";

	/* 고유 정보 { */
	@Column(name = "INDEX", nullable = false)
	private Integer index;
	@Column(name = "IMAGE_URL", nullable = false)
	private String imageUrl;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ToString.Exclude
	@JoinColumn(name = "NOTE_ID", updatable = false, nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Note note;
	/* } 연관 정보 */

	/* 생성자 { */
	protected NoteImage(Note note, int index, String imageUrl) {
		this.note = note;
		this.index = index;
		this.imageUrl = imageUrl;
		RuntimeExceptionThrower.validateDomain(this);
	}

	public static NoteImage of(Note note, int index, String imageUrl) {
		RuntimeExceptionThrower.checkIdLoaded(note);
		return new NoteImage(note, index, imageUrl);
	}
	/* } 생성자 */

	@Override public boolean isValid() {
		return this.index != null
				&& this.imageUrl != null;
	}

	/**
	 * imageUrl에서 현재 파일의 Key를 반환합니다.
	 * <p>
	 * imageUrl: [Bucket URL]/[{@link #NOTE_IMAGE_DIRECTORY}]/thisisfile.jpg
	 * <br>
	 * imageFilePath: [{@link #NOTE_IMAGE_DIRECTORY}]/thisisfile.jpg <- 처음에 '/'가 없습니다.
	 *
	 * @return imageFilePath
	 */
	@Deprecated
	public String extractImageFileKey() {
		int index = this.imageUrl.indexOf(NOTE_IMAGE_DIRECTORY);
		RuntimeExceptionThrower.ifTrue(index == -1,
				new DomainException(HttpStatus.BAD_REQUEST, "이미지의 경로가 현재 버킷과 호환되지 않습니다."));
		return this.imageUrl.substring(index);
	}

	public void specifyIndex(Integer index) {
		this.index = index;
	}

	public void updateImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
