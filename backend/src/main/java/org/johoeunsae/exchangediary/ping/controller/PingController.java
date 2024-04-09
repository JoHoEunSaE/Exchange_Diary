package org.johoeunsae.exchangediary.ping.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.Collections;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.johoeunsae.exchangediary.auth.oauth2.domain.MemberUnregisterEvent;
import org.johoeunsae.exchangediary.auth.oauth2.domain.WithdrawalReason;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterReasonDTO;
import org.johoeunsae.exchangediary.auth.oauth2.login.apple.jwt.AppleJwtService;
import org.johoeunsae.exchangediary.notice.domain.event.FollowNewEvent;
import org.johoeunsae.exchangediary.notice.domain.event.SimpleNoticeEvent;
import org.johoeunsae.exchangediary.utils.obfuscation.DataDecode;
import org.johoeunsae.exchangediary.utils.obfuscation.DataEncode;
import org.johoeunsae.exchangediary.utils.obfuscation.TargetMapping;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
@RequiredArgsConstructor
@Log4j2
public class PingController {

	private final Environment environment;
	private final ApplicationEventPublisher eventPublisher;
	private final AppleJwtService appleJwtService;

	@Operation(summary = "ping test", description = "server가 잘 올라갔는지 확인을 위한 url")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok", content = @Content(examples = @ExampleObject(value = "pong"))),

	})
	@GetMapping
	public String ping() {
		return "pong";
	}

	@PostMapping("/alarm")
	@Transactional
	public AlarmTestDto alarmTest(
			@RequestBody AlarmTestDto dto
	) {
		switch (dto.getNoticeType()) {
			case FOLLOW_CREATE_FROM: {
				log.info("test alarm: follow create from");
				eventPublisher.publishEvent(
						FollowNewEvent.builder()
								.fromId(dto.getFromId())
								.receiverId(dto.getToId())
								.fromName(dto.getFromName())
								.createdAt(LocalDateTime.now())
								.build());
			}
			case NOTE_LIKE_FROM_TO:
			case DIARY_MEMBER_FROM_TO:
			case DIARY_NOTE_FROM_TO:
			case ANNOUNCEMENT: {
				log.info("test alarm: announcement");
				eventPublisher.publishEvent(
						SimpleNoticeEvent.builder()
								.title("test title")
								.toId(dto.getToId())
								.build()
				);
			}
		}
		return dto;
	}

	@GetMapping("/alarm")
	@Transactional
	public SimpleNoticeEvent SimpleAlarm(@RequestParam Long toId) {
		SimpleNoticeEvent event = SimpleNoticeEvent.builder()
				.title("test title")
				.toId(toId)
				.build();
		eventPublisher.publishEvent(event);
		return event;
	}

	@PostMapping("/event")
	@Transactional
	@DataEncode({
			@TargetMapping(clazz = TestDto.class, fields = {TestDto.Fields.content,
					TestDto.Fields.title}),
	})
	public void hello(
			@RequestBody TestDto dto
	) {
		System.out.println("dto = " + dto);
	}

	@GetMapping("/event")
//	@DataDecode({"content", "title"})
	@DataDecode
	public TestDto hello2() {
		return new TestDto(1, "dGhpcy1pcy1jb250ZW50", "aGVsbG9Xb3JsZFRoaXNpc1RpdGxl",
				Collections.emptyList());
	}

	@GetMapping("/profile")
	public String getProfile() {
		// 현재 활성화된 프로파일 가져오기
		String[] activeProfiles = environment.getActiveProfiles();

		if (activeProfiles.length > 0) {
			return activeProfiles[0];
		} else {
			return "No active profiles";
		}
	}

	@GetMapping("/naga")
	@Transactional
	public String getNaga() {
		eventPublisher.publishEvent(
				MemberUnregisterEvent.builder()
						.reason(
								UnregisterReasonDTO.builder().reason(WithdrawalReason.OTHER_REASON)
										.otherReason("naga")
										.build()
						)
						.build()
		);
		return "ok";
	}

	@GetMapping("/path")
	public String getPath() {
		PrivateKey privateKey = appleJwtService.getPrivateKey();
		System.out.println(privateKey);
		System.out.println(privateKey.getAlgorithm());
		return "ok";
	}

}
