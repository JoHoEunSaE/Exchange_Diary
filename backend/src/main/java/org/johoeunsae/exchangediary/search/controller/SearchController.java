package org.johoeunsae.exchangediary.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.johoeunsae.exchangediary.auth.oauth2.domain.LoginUserInfo;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.dto.MemberPreviewPaginationDto;
import org.johoeunsae.exchangediary.log.Logging;
import org.johoeunsae.exchangediary.member.service.MemberQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "검색", description = "검색 관련 API")
@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
@Logging
public class SearchController {

	private final MemberQueryService memberQueryService;

	@Operation(summary = "멤버 검색", description = "멤버를 검색합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "멤버 검색 성공",
					content = @Content(schema = @Schema(implementation = MemberPreviewPaginationDto.class))),
	})
	@PreAuthorize("isAuthenticated() && !hasRole('BLACKLIST_USER')")
	@GetMapping("/members-preview")
	public MemberPreviewPaginationDto getMembersPreview(
			@LoginUserInfo UserSessionDto userSessionDto,
			@RequestParam("name") String name,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		if (size <= 0) {
			size = Integer.MAX_VALUE;
		}

		return memberQueryService.getMemberPreviewList(userSessionDto.getUserId(), name,
				PageRequest.of(page, size));
	}
}
