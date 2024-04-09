package org.johoeunsae.exchangediary.member.service.preprocessor;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.auth.oauth2.service.Oauth2Manager;
import org.johoeunsae.exchangediary.diary.service.DiaryQueryService;
import org.johoeunsae.exchangediary.diary.service.DiaryService;
import org.johoeunsae.exchangediary.exception.status.MemberExceptionStatus;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.extension.MemberPass;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class MemberPreprocessorImpl implements MemberPreprocessor {

	private final Oauth2Manager oauth2Manager;
	private final DiaryService diaryService;
	private final DiaryQueryService diaryQueryService;

	@Override
	public void delete(MemberPass member) {
		log.info("delete: member={}", member.getMember().getId());
		switch (member.getMemberType()) {
			case SOCIAL:
				deleteSocialMember(member);
				break;
			case PASSWORD:
				deleteLocalMember(member);
				break;
			case NONE:
				throw new IllegalArgumentException("잘못된 회원입니다.");
		}
	}

	private void deleteSocialMember(MemberPass member) {
		log.info("deleteSocialMember: member={}", member.getMember().getId());
		if (member.getLoginDto() == null) {
			throw MemberExceptionStatus.NEED_AUTH_CODE.toServiceException();
		}
		leaveAllMemberDiaries(member);
	}

	private void deleteLocalMember(MemberPass member) {
		log.info("deleteLocalMember: member={}", member.getMember().getId());
		leaveAllMemberDiaries(member);
	}

	private void leaveAllMemberDiaries(MemberPass memberPass) {
		Member member = memberPass.getMember();
		diaryQueryService.getMyDiaries(member.getId()).forEach(diary -> {
			diaryService.leaveDiary(member.getId(), diary.getDiaryId());
		});
	}
}
