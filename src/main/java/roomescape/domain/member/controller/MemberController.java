package roomescape.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.annotation.Authority;
import roomescape.domain.member.domain.Role;
import roomescape.domain.member.response.FindAllMemberResponse;
import roomescape.domain.member.service.MemberQueryService;

@Tag(name = "MemberController", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryService memberQueryService;

    public MemberController(MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    @Operation(summary = "Find All Member", description = "모든 회원 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindAllMemberResponse.class)))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping
    @Authority(Role.ADMIN)
    public List<FindAllMemberResponse> findAllMember() {
        return memberQueryService.findAllMemberProfile();
    }
}
