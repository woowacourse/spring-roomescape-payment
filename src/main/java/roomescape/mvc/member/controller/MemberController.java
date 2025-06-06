package roomescape.mvc.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.member.response.FindAllMemberResponse;
import roomescape.mvc.member.service.MemberQueryService;
import roomescape.annotation.Authority;
import roomescape.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.annotation.docs.DocsSuccessResponse;

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
    public List<FindAllMemberResponse> findAllMember() {
        return memberQueryService.findAllMemberProfile();
    }
}
