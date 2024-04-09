package org.johoeunsae.exchangediary.diary.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COVER_COLOR")
@Entity
public class CoverColor extends IdentityIdDomain {

	/* 고유 정보 { */
	@Column(name = "COLOR_CODE", nullable = false)
	private String colorCode;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@JoinColumn(name = "DIARY_ID", nullable = false)
	@OneToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Diary diary;

	protected CoverColor(
			Diary diary,
			String colorCode
	) {
		this.diary = diary;
		this.colorCode = colorCode;
	}

	public static CoverColor of(
			Diary diary,
			String colorCode
	) {
		RuntimeExceptionThrower.checkIdLoaded(diary);
		return new CoverColor(diary, colorCode);
	}
	/* } 생성자 */
}
