package roomescape.presentation.support.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.member.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.infrastructure.error.exception.ForbiddenException;
import roomescape.infrastructure.error.exception.JwtExtractException;
import roomescape.infrastructure.error.exception.UnauthorizedException;
import roomescape.infrastructure.security.AccessToken;
import roomescape.infrastructure.security.JwtProvider;
import roomescape.presentation.support.JwtTokenExtractor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public AdminInterceptor(final JwtTokenExtractor jwtTokenExtractor,
                            final JwtProvider jwtProvider,
                            final MemberRepository memberRepository) {
        this.jwtTokenExtractor = jwtTokenExtractor;
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request,
                             @NonNull final HttpServletResponse response,
                             @NonNull final Object handler) {
        final Member member = getMemberFromRequest(request);
        if (!member.isAdmin()) {
            throw new ForbiddenException("접근 권한이 없습니다.");
        }
        return true;
    }

    private Member getMemberFromRequest(final HttpServletRequest request) {
        final Long identifier = getIdentifier(request);
        return memberRepository.findById(identifier)
                .orElseThrow(() -> new UnauthorizedException("접근 권한이 없습니다."));
    }

    private Long getIdentifier(final HttpServletRequest request) {
        try {
            final AccessToken accessToken = jwtTokenExtractor.extract(request);
            return jwtProvider.extractIdentifier(accessToken);
        } catch (final JwtExtractException e) {
            throw new UnauthorizedException("인증 정보를 확인할 수 없습니다.", e);
        }
    }
}
