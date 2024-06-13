package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.controller.dto.request.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.global.auth.JwtManager;
import roomescape.global.exception.AuthorizationException;
import roomescape.repository.MemberRepository;

@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final JwtManager jwtManager;

    public LoginService(MemberRepository memberRepository, JwtManager jwtManager) {
        this.memberRepository = memberRepository;
        this.jwtManager = jwtManager;
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        Member member = memberRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new AuthorizationException("아이디 혹은 패스워드가 일치하지 않습니다."));

        return jwtManager.createToken(member);
    }
}
