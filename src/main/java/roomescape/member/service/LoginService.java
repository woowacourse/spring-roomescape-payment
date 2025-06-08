package roomescape.member.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.UnauthorizedException;
import roomescape.common.util.DateTime;
import roomescape.common.util.JwtTokenContainer;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.request.LoginRequest;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final JwtTokenContainer jwtTokenContainer;
    private final MemberRepository memberRepository;
    private final DateTime dateTime;

    public LoginService(JwtTokenContainer jwtTokenContainer, MemberRepository memberRepository, DateTime dateTime) {
        this.jwtTokenContainer = jwtTokenContainer;
        this.memberRepository = memberRepository;
        this.dateTime = dateTime;
    }

    @Transactional(readOnly = true)
    public String loginAndReturnToken(LoginRequest request) {
        log.info("로그인 시도 - email: {}", request.email());

        Optional<Member> loginMember = memberRepository.findByEmailAndPassword(request.email(),
                request.password());
        if (loginMember.isEmpty()) {
            log.warn("로그인 실패 - 아이디 또는 비밀번호 불일치 - email: {}", request.email());
            throw new UnauthorizedException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        String jwtToken = jwtTokenContainer.createJwtToken(loginMember.get(), dateTime.now());
        log.info("로그인 성공 - memberId: {}, email: {}", loginMember.get().getId(), request.email());
        return jwtToken;
    }

    @Transactional(readOnly = true)
    public LoginMember loginCheck(String token) {
        jwtTokenContainer.validateToken(token);
        Long memberId = jwtTokenContainer.getMemberId(token);
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            log.warn("토큰 검증 실패 - 유효하지 않은 회원 - memberId: {}", memberId);
            throw new UnauthorizedException("유효하지 않은 회원입니다.");
        }
        Member findMember = member.get();
        return new LoginMember(memberId, findMember.getName());
    }
}
