package org.johoeunsae.exchangediary.member.request;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.exception.ServiceException;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.policy.MemberPolicy;
import org.johoeunsae.exchangediary.utils.update.UpdateException;
import org.johoeunsae.exchangediary.utils.update.UpdateException.Status;
import org.johoeunsae.exchangediary.utils.update.UpdateRequest;

@Log4j2
@ToString
public class MemberUpdateRequest extends UpdateRequest<Member> {
	@Getter
	private String nickname = null;
	private String statement = null;
	private String profileFilename = null;
	private LocalDateTime now = null;
	@ToString.Exclude
	private MemberPolicy memberPolicy = null;

	@Builder
	public MemberUpdateRequest(String nickname, String statement, String profileFilename, LocalDateTime now, MemberPolicy memberPolicy) {
		setNickname(nickname);
		setStatement(statement);
		setMemberPolicy(memberPolicy);
		setNow(now);
		setProfileFilename(profileFilename);
	}
	public MemberUpdateRequest(){}

	// check state
	private void checkLoadedNow() throws UpdateException {
		if (Objects.isNull(now)) {
			log.warn("now is null");
			throw new UpdateException("now must not be null", Status.IllegalState);
		}
	}
	private void checkLoadedMemberPolicy() throws UpdateException {
		if (Objects.isNull(memberPolicy)) {
			log.warn("memberPolicy is null");
			throw new UpdateException("memberPolicy must not be null", Status.IllegalState);
		}
	}
	private void checkNickname(Member member) {
		checkLoadedNow();
		checkLoadedMemberPolicy();
		if (member.getNickname().equals(nickname)) {
			return;
		}
		if (!memberPolicy.isValidNickname(nickname)) {
			throw new ServiceException(MemberExceptionStatus.INVALID_NICKNAME);
		}
		if (!memberPolicy.isUpdatableNicknameDate(member.getNicknameUpdatedAt(), now)) {
			throw new ServiceException(MemberExceptionStatus.NOT_POSSIBLE_PERIOD);
		}
	}
	private void checkStatement(Member member) {
		checkLoadedMemberPolicy();
		if (member.getStatement().equals(statement)) {
			return;
		}
		if (!memberPolicy.isValidStatement(statement)) {
			throw new ServiceException(MemberExceptionStatus.INVALID_STATEMENT);
		}
	}

	// setter
	public void setNickname(String nickname) {
		if (nickname == null) {
			return;
		}
		this.addValidator(this::checkNickname, (e) -> e.updateNickname(nickname, now));
		this.nickname = nickname;
	}
	public void setStatement(String statement) {
		if (statement == null) {
			return;
		}
		this.addValidator(this::checkStatement, (e) -> e.updateStatement(statement));
		this.statement = statement;
	}
	public void setProfileFilename(String profileFilename) {
		if (profileFilename == null) {
			return;
		}
		this.addApplier((e) -> e.updateProfileImageUrl(profileFilename));
		this.profileFilename = profileFilename;
	}
	public void setNow(LocalDateTime now) {
		this.now = now;
	}
	public void setMemberPolicy(MemberPolicy memberPolicy) {
		this.memberPolicy = memberPolicy;
	}
}
