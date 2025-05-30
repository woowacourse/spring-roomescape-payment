package roomescape.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.LoginException;
import roomescape.common.util.DateTime;
import roomescape.common.util.JwtTokenContainer;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.dto.request.LoginRequest;

import java.util.Optional;

@Service
public class LoginService {

    private final JwtTokenContainer jwtTokenContainer;
    private final MemberRepository memberRepository;
    private final DateTime dateTime;

    public LoginService(final JwtTokenContainer jwtTokenContainer, final MemberRepository memberRepository, final DateTime dateTime) {
        this.jwtTokenContainer = jwtTokenContainer;
        this.memberRepository = memberRepository;
        this.dateTime = dateTime;
    }

    @Transactional(readOnly = true)
    public String loginAndReturnToken(final LoginRequest request) {
        Optional<Member> loginMember = memberRepository.findByEmailAndPassword(request.email(),
                request.password());
        if (loginMember.isEmpty()) {
            throw new LoginException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenContainer.createJwtToken(loginMember.get(), dateTime.now());
    }

    @Transactional(readOnly = true)
    public String findMemberName(final long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));
        return findMember.getName();
    }
}
