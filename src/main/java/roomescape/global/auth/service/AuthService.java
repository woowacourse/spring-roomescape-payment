package roomescape.global.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.global.auth.dto.LoginResponse;
import roomescape.global.auth.dto.UserInfo;
import roomescape.global.auth.exception.UnAuthorizedException;
import roomescape.global.auth.infrastructure.JwtProvider;
import roomescape.member.domain.Member;
import roomescape.member.repository.JpaMemberRepository;

@Slf4j
@Component
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JpaMemberRepository memberRepository;
    private final MyPasswordEncoder myPasswordEncoder;

    public AuthService(final JwtProvider jwtProvider,
                       final roomescape.member.repository.JpaMemberRepository memberRepository,
                       final MyPasswordEncoder myPasswordEncoder) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
        this.myPasswordEncoder = myPasswordEncoder;
    }

    public LoginResponse login(final LoginRequest loginRequest) {
        Member member = findValidMember(loginRequest.email(), loginRequest.password());
        String accessToken = jwtProvider.createToken(UserInfo.from(member));
        return new LoginResponse(accessToken);
    }

    private Member findValidMember(final String email, final String password) {
        Member member = findMemberByEmail(email);
        checkPassword(password, member);
        return member;
    }

    public UserInfo makeUserInfo(final String token) {
        validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);
        return new UserInfo(memberId, jwtProvider.getRole(token));
    }

    private Member findMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("존재하지 이메일 정보로 로그인 실패 email = {}", email);
                    return new UnAuthorizedException("존재하지 않은 사용자입니다.");
                });
    }

    private void validateToken(final String token) {
        if (jwtProvider.isInvalidToken(token)) {
            log.info("유효하지 않은 토큰 token = {}", token);
            throw new UnAuthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    private void checkPassword(final String password, final Member member) {
        if (!myPasswordEncoder.matches(password, member.getPassword())) {
            log.info("패스워드 불일치로 로그인 실패 email = {}", member.getEmail());
            throw new UnAuthorizedException("로그인에 실패하였습니다.");
        }
    }
}
