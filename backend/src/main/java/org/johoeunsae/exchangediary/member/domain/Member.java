package org.johoeunsae.exchangediary.member.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.johoeunsae.exchangediary.blacklist.domain.Blacklist;
import org.johoeunsae.exchangediary.bookmark.domain.Bookmark;
import org.johoeunsae.exchangediary.diary.domain.Diary;
import org.johoeunsae.exchangediary.exception.utils.RuntimeExceptionThrower;
import org.johoeunsae.exchangediary.notice.domain.DeviceRegistry;
import org.johoeunsae.exchangediary.utils.DateUtil;
import org.johoeunsae.exchangediary.utils.domain.IdentityIdDomain;
import org.johoeunsae.exchangediary.utils.domain.Validatable;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = Member.DTYPE)
@Table(name = "MEMBER")
@Entity
public class Member extends IdentityIdDomain implements Validatable {

	static final public String DTYPE = "LOGIN_TYPE";
	static final public String PASSWORD_MEMBER = "PASSWORD";
	static final public String SOCIAL_MEMBER = "SOCIAL";

	/* 연관 정보 { */
	@ToString.Exclude
	@OneToMany(
			mappedBy = "member",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private final List<Blacklist> blackLists = new ArrayList<>();
	@ToString.Exclude
	@OneToMany(
			mappedBy = "member",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private final List<DeviceRegistry> deviceRegistries = new ArrayList<>();

	@ToString.Exclude
	@OneToMany(
			mappedBy = "masterMember",
			fetch = FetchType.LAZY)
	private final List<Diary> managingDiaries = new ArrayList<>();
	@ToString.Exclude
	@OneToMany(
			mappedBy = "member",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL)
	private final List<Bookmark> bookmarks = new ArrayList<>();
	/* } 연관 정보 */

	/* 고유 정보 { */
	// nullable = false
	@Column(name = "NICKNAME", length = 15, nullable = false, unique = true)
	private String nickname;
	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;
	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE", nullable = false, length = 20)
	private MemberRole role;
	@Column(name = "NICKNAME_UPDATED_AT", nullable = false)
	private LocalDateTime nicknameUpdatedAt;
	@Column(name = "LAST_LOGGED_IN_AT", nullable = false)
	private LocalDateTime lastLoggedInAt;
	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;
	// nullable
	@Column(name = "DELETED_AT")
	private LocalDateTime deletedAt = null;
	@Column(name = "STATEMENT", length = 63)
	private String statement = "";
	@Column(name = "PROFILE_IMAGE_URL", length = 255)
	private String profileImageUrl = null;

	@Column(name = Member.DTYPE, insertable = false, updatable = false)
	private String dtype;
	/* } 고유 정보 */

	/* 생성자 { */
	protected Member(String nickname, String email, MemberRole role, LocalDateTime now) {
		this.nickname = nickname;
		this.email = email;
		this.role = role;
		this.createdAt = now;
		this.nicknameUpdatedAt = DateUtil.getMinDate();
		this.lastLoggedInAt = now;
		if (this instanceof PasswordMember) {
			this.dtype = PASSWORD_MEMBER;
		} else if (this instanceof SocialMember) {
			this.dtype = SOCIAL_MEMBER;
		} else {
			this.dtype = null;
		}
	}

	public static Member of(MemberFeatures memberFeatures, MemberRole role, LocalDateTime now) {
		return new Member(memberFeatures.getNickname(), memberFeatures.getEmail(), role, now);
	}

	public static SocialMember createSocialMember(
			MemberFeatures identity, LocalDateTime now, OauthInfo oauthInfo
	) {
		return SocialMember.of(
				identity.getNickname(), identity.getEmail(), MemberRole.USER,
				now, oauthInfo.getOauthId(), oauthInfo.getOauthType());
	}

	public static PasswordMember createPasswordMember(
			MemberFeatures identity, LocalDateTime now, PasswordInfo passwordInfo) {
		return PasswordMember.of(identity, MemberRole.USER, now, passwordInfo);
	}
	/* 생성자 { */

	/* update { */
	public void login(LocalDateTime now) {
		this.lastLoggedInAt = now;
	}

	public void updateNickname(String nickname, LocalDateTime now) {
		if (isEqualNickname(nickname)) {
			return;
		}
		this.nickname = nickname;
		this.nicknameUpdatedAt = now;
	}

	public void updateStatement(String statement) {
		if (statement.equals(this.statement)) {
			return;
		}
		this.statement = statement;
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void updateRole(MemberRole role) {
		this.role = role;
	}

	public void changeDefaultProfileImage() {
		this.profileImageUrl = null;
	}

	public void delete(LocalDateTime now) {
		this.deletedAt = now;
	}
	/* } update */

	/* 편의 메서드 { */
	public void removeDeviceToken(DeviceRegistry deviceRegistry) {
		RuntimeExceptionThrower.checkIdLoaded(deviceRegistry);
		getDeviceRegistries().remove(deviceRegistry);
	}

	public boolean isEqualNickname(String nickname) {
		return nickname.equals(this.nickname);
	}

	public boolean isEqualProfileImageUrl(String profileImageUrl) {
		if (Objects.isNull(this.profileImageUrl)) {
			return Objects.isNull(profileImageUrl);
		}
		return profileImageUrl.equals(this.profileImageUrl);
	}
	/* } 편의 메서드 */

	@Override
	public boolean isValid() {
		final Pattern emailPattern = Pattern.compile(
				"^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");
		if (nickname == null || email == null || role == null || nicknameUpdatedAt == null
				|| lastLoggedInAt == null || createdAt == null) {
			return false;
		}
		return emailPattern.matcher(email).matches();
	}

	public boolean isActive() {
		return deletedAt == null;
	}
}
