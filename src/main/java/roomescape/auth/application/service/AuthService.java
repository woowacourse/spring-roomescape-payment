package roomescape.auth.application.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.auth.exception.InvalidEmailException;
import roomescape.auth.exception.InvalidPasswordException;
import roomescape.auth.exception.UnauthorizedException;
import roomescape.auth.infrastructure.TokenProvider;
import roomescape.auth.presentation.dto.LoginCheckResponse;
import roomescape.auth.presentation.dto.LoginMember;
import roomescape.auth.presentation.dto.LoginRequest;
import roomescape.global.exception.ForbiddenException;
import roomescape.global.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String MEMBER_ID = "memberId";

    private final TokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public String createToken(final LoginRequest loginRequest) {
        Member member = getMemberByLoginRequest(loginRequest);
        return jwtTokenProvider.createToken(member);
    }

    public Member getMemberByLoginRequest(final LoginRequest request) {
        String email = request.email();
        Member member = getMemberByEmail(email);
        validateMemberPassword(request, member);
        return member;
    }

    private void validateMemberPassword(LoginRequest request, Member member) {
        if (!member.matchesPassword(request.password())) {
            log.warn("비밀번호 불일치 시도 - 회원 ID: {}", member.getId());
            throw new InvalidPasswordException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public LoginCheckResponse checkLogin(final String token) {
        final Long memberId = parseMemberId(token);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 회원입니다. id: " + memberId));
        return new LoginCheckResponse(member);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("인증 실패: 존재하지 않는 이메일 - email: {}", email);
                    return new InvalidEmailException("등록이 되지 않은 유저 이메일 입니다.");
                });
    }

    private Long parseMemberId(final String token) {
        try {
            return Long.valueOf(jwtTokenProvider.extractLoginMember(token));
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    public void checkAdmin(final HttpServletRequest request) {
        final LoginMember loginMember = extractMemberByRequest(request);
        if (!loginMember.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
    }

    public LoginMember extractMemberByRequest(final HttpServletRequest request) {
        try {
            return findMemberByMemberId(extractMemberId(request));
        } catch (IllegalArgumentException | NotFoundException e) {
            log.debug(e.getMessage());
            throw new UnauthorizedException("인증에 실패했습니다.");
        }
    }

    private Long extractMemberId(final HttpServletRequest request) {
        final Object raw = request.getAttribute(MEMBER_ID);
        return Optional.ofNullable(raw)
                .filter(Long.class::isInstance)
                .map(Long.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("memberId 형식이 올바르지 않습니다. memberId =" + raw));
    }

    private LoginMember findMemberByMemberId(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. memberId =" + memberId));
        return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
    }
}
