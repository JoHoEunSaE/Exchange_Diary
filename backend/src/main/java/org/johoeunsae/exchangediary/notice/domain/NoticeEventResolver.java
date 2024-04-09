package org.johoeunsae.exchangediary.notice.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.cloud.aws.domain.SqsNoticeMessage;
import org.johoeunsae.exchangediary.diary.domain.Registration;
import org.johoeunsae.exchangediary.diary.repository.RegistrationRepository;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.repository.MemberRepository;
import org.johoeunsae.exchangediary.notice.domain.event.DiaryMasterChangedEvent;
import org.johoeunsae.exchangediary.notice.domain.event.DiaryMemberKickEvent;
import org.johoeunsae.exchangediary.notice.domain.event.DiaryNewMemberEvent;
import org.johoeunsae.exchangediary.notice.domain.event.FollowNewEvent;
import org.johoeunsae.exchangediary.notice.domain.event.NoticeEvent;
import org.johoeunsae.exchangediary.notice.domain.event.SimpleNoticeEvent;
import org.johoeunsae.exchangediary.notice.repository.DeviceRegistryRepository;
import org.springframework.stereotype.Component;

/**
 * NoticeEvent를 식별하여 Message로 반환하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class NoticeEventResolver {

	private final RegistrationRepository registrationRepository;
	private final MemberRepository memberRepository;
	private final DeviceRegistryRepository deviceRegistryRepository;

	/**
	 * NoticeEvent를 Message로 변환합니다.
	 *
	 * @param event {@link NoticeEvent}
	 * @return Message
	 */
	public List<Message> convert(NoticeEvent event) {
		if (event instanceof FollowNewEvent) {
			return convertToMessages((FollowNewEvent) event);
		}
		if (event instanceof DiaryNewMemberEvent) {
			return convertToMessages((DiaryNewMemberEvent) event);
		}
		if (event instanceof DiaryMasterChangedEvent) {
			return convertToMessages((DiaryMasterChangedEvent) event);
		}
		if (event instanceof DiaryMemberKickEvent) {
			return convertToMessages((DiaryMemberKickEvent) event);
		}
		if (event instanceof SimpleNoticeEvent) {
			return convertToMessages((SimpleNoticeEvent) event);
		}

		throw new UnsupportedOperationException();
	}

	private List<Message> convertToMessages(SimpleNoticeEvent event) {
		return List.of(SqsNoticeMessage.builder()
				.title(event.getTitle())
				.format(event.getFormat())
				.noticeType(event.getNoticeType())
						.receiverId(event.getToId())
				.attributes(event.getAttributesAsString())
				.parameters(event.getDeepLinkParameters())
				.createdAt(LocalDateTime.now())
				.build());
	}

	private List<Message> convertToMessages(FollowNewEvent event) {
		SqsNoticeMessage message = SqsNoticeMessage.builder()
				.title(event.getTitle())
				.format(event.getFormat())
				.noticeType(event.getNoticeType())
				.receiverId(event.getReceiverId())
				.attributes(event.getAttributesAsString())
				.parameters(event.getDeepLinkParameters())
				.createdAt(event.getCreatedAt())
				.build();

		return List.of(message);
	}

	private List<Message> convertToMessages(DiaryNewMemberEvent event) {
		List<Long> receiverIds = getNoticeReceiverIdsByDiaryId(event.getDiaryId());
		receiverIds.removeIf(id -> id.equals(event.getNewMemberId()));
		return receiverIds.stream().map(id ->
				SqsNoticeMessage.builder()
						.title(event.getTitle())
						.format(event.getFormat())
						.noticeType(event.getNoticeType())
						.receiverId(id)
						.attributes(event.getAttributesAsString())
						.parameters(event.getDeepLinkParameters())
						.createdAt(event.getCreatedAt())
						.build()
		).collect(Collectors.toList());
	}

	private List<Message> convertToMessages(DiaryMasterChangedEvent event) {
		List<Long> receiverIds = getNoticeReceiverIdsByDiaryId(event.getDiaryId());
		receiverIds.removeIf(id -> id.equals(event.getChangedMasterId()));
		return receiverIds.stream().map(id ->
				SqsNoticeMessage.builder()
						.title(event.getTitle())
						.format(event.getFormat())
						.noticeType(event.getNoticeType())
						.receiverId(event.getChangedMasterId())
						.attributes(event.getAttributesAsString())
						.parameters(event.getDeepLinkParameters())
						.createdAt(event.getCreatedAt())
						.build()
		).collect(Collectors.toList());
	}

	private List<Message> convertToMessages(DiaryMemberKickEvent event) {
		return List.of(
				SqsNoticeMessage.builder()
						.title(event.getTitle())
						.format(event.getFormat())
						.noticeType(event.getNoticeType())
						.receiverId(event.getReceiverId())
						.attributes(event.getAttributesAsString())
						.parameters(event.getDeepLinkParameters())
						.createdAt(event.getCreatedAt())
						.build()
		);
	}

	private List<Long> getNoticeReceiverIdsByDiaryId(Long diaryId) {
		List<Registration> registrations = registrationRepository.findByDiaryId(diaryId);
		return registrations.stream()
				.map(Registration::getMember)
				.map(Member::getId).collect(Collectors.toList());
	}
}