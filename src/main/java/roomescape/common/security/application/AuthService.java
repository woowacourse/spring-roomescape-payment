package roomescape.common.security.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.common.security.dto.request.LoginRequest;
import roomescape.common.security.dto.request.MemberInfo;
import roomescape.common.security.dto.response.LoginResponse;
import roomescape.common.security.exception.UnAuthorizedException;
import roomescape.common.security.infrastructure.JwtProvider;
import roomescape.member.domain.Member;
import roomescape.member.infrastructure.MemberRepository;

@Component
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final MyPasswordEncoder myPasswordEncoder;

    public AuthService(final JwtProvider jwtProvider, final MemberRepository memberRepository,
                       final MyPasswordEncoder myPasswordEncoder) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
        this.myPasswordEncoder = myPasswordEncoder;
    }

    public LoginResponse login(final LoginRequest loginRequest) {
        log.info("로그인 시도: email={}", loginRequest.email());
        Member member = findValidMember(loginRequest.email(), loginRequest.password());
        String accessToken = jwtProvider.createToken(MemberInfo.from(member));
        log.info("로그인 성공: memberId={}", member.getId());
        return new LoginResponse(accessToken);
    }

    public MemberInfo makeMemberInfo(final String token) {
        validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);
        return new MemberInfo(memberId, jwtProvider.getRole(token));
    }

    private Member findValidMember(final String email, final String password) {
        Member member = findMemberByEmail(email);
        checkPassword(password, member);
        return member;
    }

    private Member findMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 사용자: email={}", email);
                    return new UnAuthorizedException("존재하지 않은 사용자입니다.");
                });
    }

    private void checkPassword(final String password, final Member member) {
        if (!myPasswordEncoder.matches(password, member.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: email={}", member.getEmail());
            throw new UnAuthorizedException("로그인에 실패하였습니다.");
        }
    }

    private void validateToken(final String token) {
        if (jwtProvider.isInvalidToken(token)) {
            log.warn("토큰 검증 실패: 유효하지 않은 토큰");
            throw new UnAuthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}
