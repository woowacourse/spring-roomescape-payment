package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.auth.RoleRequired;
import roomescape.member.controller.dto.MemberInfoResponse;
import roomescape.member.domain.Role;
import roomescape.member.service.AccountMemberService;

@RequiredArgsConstructor
@RestController
//@Tag(name = "Auth", description = "인증을 위한 api")
public class MemberController {

    private final AccountMemberService accountMemberService;

    @Operation(summary = "모든 회원 조회", description = "모든 회원 정보를 조회합니다.")
    @RoleRequired(value = Role.ADMIN)
    @GetMapping("/members")
    public List<MemberInfoResponse> getMembers() {
        return accountMemberService.getAll();
    }
}
