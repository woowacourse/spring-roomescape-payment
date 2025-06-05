package roomescape.member.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.dto.request.MemberCreateRequest;
import roomescape.member.dto.response.MemberCreateResponse;
import roomescape.member.dto.response.MemberReadResponse;
import roomescape.member.entity.RoleType;
import roomescape.member.service.MemberService;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입", description = "새로운 회원을 생성합니다.")
    @PostMapping
    public ResponseEntity<MemberCreateResponse> createMember(
            @RequestBody @Valid MemberCreateRequest request
    ) {
        MemberCreateResponse response = memberService.createMember(request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모든 회원 조회", description = "현재 등록된 모든 회원 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MemberReadResponse>> getAllMembers() {
        List<MemberReadResponse> responses = memberService.getAllMembers();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "회원 삭제", description = "관리자 권한으로 회원을 삭제합니다.")
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteMember(
            @PathVariable("id") long id
    ) {
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }
}
