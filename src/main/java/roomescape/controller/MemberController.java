package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.configuration.annotation.Authority;
import roomescape.configuration.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.configuration.annotation.docs.DocsSuccessResponse;
import roomescape.domain.Role;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.service.query.MemberQueryService;

@Tag(name = "MemberController", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryService memberQueryService;

    public MemberController(MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    @Operation(summary = "Find All Member", description = "모든 회원 조회")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @GetMapping
    @Authority(Role.ADMIN)
    public List<MemberProfileResponse> findAllMember() {
        return memberQueryService.findAllMemberProfile();
    }
}
