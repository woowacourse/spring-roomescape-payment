package roomescape.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.auth.LoginMember;
import roomescape.member.auth.PermitAll;
import roomescape.member.auth.RoleRequired;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Role;

@RestController
public class TestAuthController {

    @PermitAll
    @GetMapping("/test/permit-all")
    public void permitAll() {
    }

    @RoleRequired(Role.ADMIN)
    @GetMapping("/test/role-required-admin")
    public void adminRequired() {
    }

    @GetMapping("/test/login-member")
    public MemberInfo loginMember(@LoginMember MemberInfo memberInfo) {
        return memberInfo;
    }

    @GetMapping("/admin/test")
    public void adminPath() {
    }
}
