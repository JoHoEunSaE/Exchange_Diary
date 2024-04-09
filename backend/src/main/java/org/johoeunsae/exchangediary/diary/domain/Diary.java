package org.johoeunsae.exchangediary.diary.domain;

import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.note.domain.Note;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DIARY")
@Entity
public class Diary extends IdentityIdDomain {

	/* public 정보 { */
	public static final Long DEFAULT_DIARY_ID = 0L;

	/* } public 정보 */

	/* 고유 정보 { */
	@Column(name = "TITLE", length = 31, nullable = false)
	private String title;
	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	@Column(name = "UPDATED_AT", nullable = false)
	private LocalDateTime updatedAt;
	@Column(name = "GROUP_NAME", length = 15)
	private String groupName;
	/* } 고유 정보 */

	/* 연관 정보 { */

	@JoinColumn(name = "MASTER_MEMBER_ID", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Member masterMember;

	@ToString.Exclude
	@OneToMany(
			mappedBy = "diary",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private final List<Registration> registrations = new ArrayList<>();

	@OneToOne(mappedBy = "diary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private CoverImage coverImage;

	@OneToOne(mappedBy = "diary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private CoverColor coverColor;

	@Column(name = "COVER_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private CoverType coverType;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Diary(
			Member master,
			LocalDateTime createdAt,
			LocalDateTime updatedAt,
			String title, String groupName,
			CoverType coverType) {
		this.title = title;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.groupName = groupName;
		this.masterMember = master;
		this.coverType = coverType;
	}

	public static Diary of(
			Member master, LocalDateTime now, String title, String groupName, CoverType coverType) {
		RuntimeExceptionThrower.checkIdLoaded(master);
		return new Diary(master, now, now, title, groupName, coverType);
	}

	/* } 생성자 */

	public boolean isMaster(Member member) {
		return this.getMasterMember().equals(member);
	}

	public void changeCoverType(CoverType coverType) {
		this.coverType = coverType;
	}

	/* 편의 메서드 { */
	public void setMasterMember(Member member) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		if (this.getMasterMember() != null) {
			this.getMasterMember().getManagingDiaries().remove(this);
		}
		this.masterMember = member;
		member.getManagingDiaries().add(this);
	}

	public void removeRegistration(Registration registration) {
		RuntimeExceptionThrower.checkIdLoaded(registration);
		this.getRegistrations().remove(registration);
	}

	public void removeMember(Member member) {
		RuntimeExceptionThrower.checkIdLoaded(member);
		this.getRegistrations()
				.stream().filter(registration -> registration.getMember().equals(member))
				.findFirst().ifPresent(this::removeRegistration);
	}

	public void updateTitle(String title) {
		if (this.title.equals(title)) {
			return;
		}
		this.title = title;
	}

	public void updateGroupName(String groupName) {
		if (this.groupName == null) {
			this.groupName = groupName;
			return;
		}
		if (this.groupName.equals(groupName)) {
			return;
		}
		this.groupName = groupName;
	}
	/* } 편의 메서드 */

	/* 연관관계 편의 메서드 { */
	public void setCoverImage(CoverImage coverImage) {
		this.coverImage = coverImage;
	}

	public void setCoverColor(CoverColor coverColor) {
		this.coverColor = coverColor;
	}

	public boolean isDiaryMember(Member member) {
		return this.getRegistrations()
				.stream().map(Registration::getMember)
				.anyMatch(member::equals);
	}

	public boolean isDiaryMember(Long memberId) {
		return this.getRegistrations()
				.stream()
				.map(Registration::getMember)
				.anyMatch(member -> memberId.equals(member.getId()));
	}

	public boolean isBelongToDiary(Note note) {
		return this.getId().equals(note.getDiaryId());
	}

	public void changeMaster(Member targetMember) {
		this.setMasterMember(targetMember);
	}

	public String getCoverData() {
		switch (this.coverType) {
			case IMAGE:
				return this.coverImage.getImageUrl();
			case COLOR:
				return this.coverColor.getColorCode();
			default:
				return null;
		}
	}

	/* } 연관관계 편의 메서드 */
}
