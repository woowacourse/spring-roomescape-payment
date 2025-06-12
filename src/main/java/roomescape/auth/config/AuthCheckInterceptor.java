package roomescape.auth.config;

import static roomescape.global.exception.ErrorMessage.INTERNAL_SERVER_ERROR;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.application.AuthService;
import roomescape.auth.application.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCheckInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String clientIP = request.getRemoteAddr();

        if (request.getCookies() == null) {
            log.warn("[인증 실패] 쿠키 정보 없음 - URI: {}, IP: {}", uri, clientIP);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        try {
            LoginMember loginMember = authService.extractMemberByRequest(request);
            if (loginMember.isNotAdmin()) {
                log.warn("[권한 부족] 관리자 권한 없음 - URI: {}, memberId: {}, role: {}, IP: {}",
                        uri, loginMember.getId(), loginMember.getRole(), clientIP);

                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }

            if (isNotAdminMember(loginMember)) {
                log.warn("[권한 검증 실패] 관리자 부가 검증 실패 - URI: {}, memberId: {}, IP: {}",
                        uri, loginMember.getId(), clientIP);

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }

            log.info("[관리자 인증 성공] - URI: {}, memberId: {}, role: {}",
                    uri, loginMember.getId(), loginMember.getRole());
            return true;
        } catch (Exception e) {
            log.error("[인증 처리 오류] URI: {}, IP: {}, error: {}",
                    uri, clientIP, e.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    private boolean isNotAdminMember(LoginMember loginMember) {
        Member member = getMemberById(loginMember.getId());
        return member.isNotAdmin();
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("[권한 검증 실패] 존재하지 않는 회원 ID: {}", memberId);
                    return new MemberNotFoundException(INTERNAL_SERVER_ERROR.getMessage());
                });
    }
}
