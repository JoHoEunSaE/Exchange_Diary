package org.johoeunsae.exchangediary.diary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COVER_IMAGE")
@Entity
public class CoverImage extends IdentityIdDomain {

	/* 고유 정보 { */
	@Column(name = "IMAGE_URL", nullable = false)
	private String imageUrl;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "DIARY_ID", nullable = false)
	@OneToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Diary diary;

	/* } 연관 정보 */

	/* 생성자 { */
	protected CoverImage(
			Diary diary,
			String imageUrl
	) {
		this.diary = diary;
		this.imageUrl = imageUrl;
	}

	public static CoverImage of(
			Diary diary,
			String imageUrl
	) {
		RuntimeExceptionThrower.checkIdLoaded(diary);
		return new CoverImage(diary, imageUrl);
	}
	/* } 생성자 */
}
