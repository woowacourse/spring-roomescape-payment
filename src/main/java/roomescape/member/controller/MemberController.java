package roomescape.member.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.SaveMemberRequest;
import roomescape.member.model.Member;
import roomescape.member.service.MemberService;

@RestController
@Tag(name = "사용자(관리자, 회원)", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    @Operation(summary = "사용자 조회", description = "모든 사용자들을 조회하는 API")
    public List<MemberResponse> getMembers() {
        return memberService.getMembers()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @PostMapping("/members")
    @Operation(summary = "사용자 추가", description = "회원가입 시 사용자를 추가하는 API")
    @ApiResponse(responseCode = "201", description = "사용자 추가 추가 성공")
    public MemberResponse saveMember(@RequestBody final SaveMemberRequest request) {
        final Member savedMember = memberService.saveMember(request);
        return MemberResponse.from(savedMember);
    }
}
