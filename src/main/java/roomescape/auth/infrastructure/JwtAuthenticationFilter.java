package roomescape.auth.infrastructure;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.auth.infrastructure.util.CookieManager;
import roomescape.exception.UnauthorizedException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String MEMBER_ID_ATTRIBUTE = "memberId";

    private static final List<String> STATIC_RESOURCES = List.of(
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            "/docs/"
    );

    private static final List<String> WHITELIST = List.of(
            "/login",
            "/logout",
            "/signup",
            "/h2-console"
    );


    private final JwtTokenProvider jwtTokenProvider;
    private final CookieManager cookieManager;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = cookieManager.extractLoginToken(request.getCookies());

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String subject = jwtTokenProvider.extractPrincipal(token);
            Long memberId = Long.valueOf(subject);
            request.setAttribute(MEMBER_ID_ATTRIBUTE, memberId);
        } catch (Exception e) {
            handleException(response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        log.error("인증 중 오류 발생", e);

        if (e instanceof UnauthorizedException) {
            writeJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }
        if (e instanceof JwtException) {
            writeJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다.");
            return;
        }
        if (e instanceof NumberFormatException) {
            writeJsonErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "잘못된 요청입니다.");
            return;
        }
        writeJsonErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
    }

    private void writeJsonErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final String body = String.format("{\"status\": %d, \"message\": \"%s\"}", status, message);
        response.getWriter().write(body);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String path = request.getRequestURI();

        if (STATIC_RESOURCES.stream().anyMatch(path::startsWith)) {
            return true;
        }
        return WHITELIST.stream().anyMatch(path::startsWith);
    }
}
