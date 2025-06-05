package roomescape.auth.application;

import org.springframework.stereotype.Service;
import roomescape.admin.application.AdminService;
import roomescape.admin.domain.Admin;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.auth.exception.UnauthorizedException;
import roomescape.auth.token.JwtTokenManager;
import roomescape.member.application.MemberService;
import roomescape.member.domain.Member;

@Service
public class LoginService {

    private final JwtTokenManager jwtTokenManager;
    private final AdminService adminService;
    private final MemberService memberService;

    public LoginService(final JwtTokenManager jwtTokenManager, final AdminService adminService,
                        final MemberService memberService) {
        this.jwtTokenManager = jwtTokenManager;
        this.adminService = adminService;
        this.memberService = memberService;
    }

    private static void validateAdminSamePassword(final LoginRequest request, final Admin admin) {
        if (!admin.isSamePassword(request.password())) {
            throw new UnauthorizedException("비밀번호가 틀립니다.");
        }
    }

    private static void validateMemberSamePassword(final LoginRequest request, final Member member) {
        if (!member.isSamePassword(request.password())) {
            throw new UnauthorizedException("비밀번호가 틀립니다.");
        }
    }

    public String createAdminToken(final LoginRequest request) {
        validateAdminExistsAccount(request);

        Admin admin = adminService.findByEmail(request.email());
        validateAdminSamePassword(request, admin);

        return jwtTokenManager.createToken(admin.getId(), "ADMIN");
    }

    public String createMemberToken(final LoginRequest request) {
        validateMemberExistsAccount(request);

        Member member = memberService.findByEmail(request.email());
        validateMemberSamePassword(request, member);

        return jwtTokenManager.createToken(member.getId(), "MEMBER");
    }

    private void validateAdminExistsAccount(final LoginRequest request) {
        boolean adminExist = adminService.isExistsByEmail(request.email());
        if (!adminExist) {
            throw new UnauthorizedException("계정이 존재하지 않습니다.");
        }
    }

    private void validateMemberExistsAccount(final LoginRequest request) {
        boolean memberExist = memberService.isExistsByEmail(request.email());
        if (!memberExist) {
            throw new UnauthorizedException("계정이 존재하지 않습니다.");
        }
    }

    public Admin findByAdminId(final Long id) {
        return adminService.findById(id);
    }

    public Member findByMemberId(final Long id) {
        return memberService.findById(id);
    }
}
