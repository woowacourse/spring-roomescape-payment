package roomescape.system.auth.service;

import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.system.auth.dto.LoginCheckResponse;
import roomescape.system.auth.dto.LoginRequest;
import roomescape.system.auth.jwt.JwtHandler;
import roomescape.system.auth.jwt.dto.TokenDto;

@Service
public class AuthService {

    private final MemberService memberService;
    private final JwtHandler jwtHandler;

    public AuthService(MemberService memberService, JwtHandler jwtHandler) {
        this.memberService = memberService;
        this.jwtHandler = jwtHandler;
    }

    public TokenDto login(LoginRequest request) {
        Member member = memberService.findMemberByEmailAndPassword(request.email(), request.password());

        return jwtHandler.createToken(member.getId());
    }

    public LoginCheckResponse checkLogin(Long memberId) {
        Member member = memberService.findMemberById(memberId);

        return new LoginCheckResponse(member.getName());
    }
}
