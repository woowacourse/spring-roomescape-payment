package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.annotation.AdminMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.auth.SignUpRequestDto;
import roomescape.dto.member.MemberResponseDto;
import roomescape.dto.member.MemberSignupResponseDto;
import roomescape.global.Loggable;
import roomescape.service.command.MemberCommandService;
import roomescape.service.query.MemberQueryService;

import java.util.List;

@Tag(name = "회원 관리 API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    public MemberController(MemberQueryService memberQueryService, MemberCommandService memberCommandService) {
        this.memberQueryService = memberQueryService;
        this.memberCommandService = memberCommandService;
    }

    @Operation(summary = "모든 회원 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MemberResponseDto> getMembers(
            @AdminMember LoginInfo loginInfo
    ) {
        return memberQueryService.findAllMembers();
    }

    @Loggable
    @Operation(summary = "회원 가입")
    @ApiResponse(responseCode = "200", description = "가입 성공")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public MemberSignupResponseDto signup(
            @RequestBody SignUpRequestDto requestDto
    ) {
        return memberCommandService.registerMember(requestDto);
    }
}
