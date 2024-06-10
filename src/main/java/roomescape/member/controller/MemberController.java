package roomescape.member.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.dto.response.FindMembersResponse;
import roomescape.member.service.MemberService;

@Tag(name = "사용자 API", description = "사용자 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "사용자 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<FindMembersResponse>> getMembers() {
        return ResponseEntity.ok(memberService.getMembers());
    }
}
