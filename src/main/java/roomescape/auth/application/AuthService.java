package roomescape.auth.application;

import static roomescape.global.exception.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.auth.exception.InvalidPasswordException;
import roomescape.auth.infrastructure.TokenProvider;
import roomescape.auth.presentation.CookieManager;
import roomescape.auth.presentation.dto.LoginRequest;
import roomescape.member.domain.Member;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CookieManager cookieManager;
    private final TokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public String createToken(final LoginRequest loginRequest) {
        Member member = getMemberByLoginRequest(loginRequest);
        return jwtTokenProvider.createToken(member);
    }

    public Member getMemberByLoginRequest(final LoginRequest request) {
        String email = request.email();

        Member member = getMemberByEmail(email);
        validateMemberPassword(request.password(), member);
        return member;
    }

    private void validateMemberPassword(String password, Member member) {
        if (!member.matchesPassword(password)) {
            log.warn("인증 실패 : 비밀번호 불일치 - memberId: {}, password: {}, input: {}",
                    member.getId(),
                    member.getPassword(),
                    password
            );
            throw new InvalidPasswordException(INVALID_LOGIN_CREDENTIALS.getMessage());
        }
    }

    private Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("인증 실패 : 존재하지 않는 이메일 - email: {}", email);
                    return new MemberNotFoundException(INVALID_LOGIN_CREDENTIALS.getMessage());
                });
    }

    public LoginMember extractMemberByRequest(final HttpServletRequest request) {
        String token = cookieManager.extractLoginToken(request.getCookies());
        return jwtTokenProvider.extractLoginMember(token);
    }
}
