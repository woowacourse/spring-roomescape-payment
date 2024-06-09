package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.dto.MembersResponse;
import roomescape.member.service.MemberService;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.dto.response.RoomEscapeApiResponse;

@RestController
@Tag(name = "3. 회원 API", description = "회원 정보를 조회하는 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Admin
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 회원 조회", description = "관리자가 모든 회원의 정보를 조회합니다.", tags = "관리자")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<MembersResponse> getAllMembers() {
        return RoomEscapeApiResponse.success(memberService.findAllMembers());
    }
}
