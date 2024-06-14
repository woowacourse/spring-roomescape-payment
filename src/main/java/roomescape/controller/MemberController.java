package roomescape.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.dto.MemberResponse;
import roomescape.service.MemberService;

@Tag(name = "사용자 API", description = "사용자 API 입니다.")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "사용자 조회", description = "전체 사용자를 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findAllMembers() {
        List<MemberResponse> loginMemberResponses = memberService.findAll();
        return ResponseEntity.ok(loginMemberResponses);
    }
}
