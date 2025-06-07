package roomescape.logging;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import roomescape.domain.auth.TokenBody;
import roomescape.infrastructure.auth.JwtProvider;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final HttpServletRequest httpServletRequest;
    private final JwtProvider jwtProvider;

    @Around("@within(org.springframework.stereotype.Service) && (execution(* create*(..)) || execution(* delete*(..)))")
    public Object logging(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodName = joinPoint.getSignature().toShortString();
        final String ip = httpServletRequest.getRemoteAddr();
        final int port = httpServletRequest.getRemotePort();
        final String requestURI = httpServletRequest.getRequestURI();
        final String tokenBody = getTokenBody();
        final String params = getParams(joinPoint.getArgs());

        final long startMillis = System.currentTimeMillis();
        final Object result = joinPoint.proceed();
        final long durationMillis = System.currentTimeMillis() - startMillis;

        log.info("{} {} {} {} {}ms {} {} {}",
                ip, port, methodName, requestURI, durationMillis, tokenBody, params, result
        );

        return result;
    }

    private String getParams(final Object[] args) {
        return Arrays.stream(args)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private String getTokenBody() {
        final String token = Optional.ofNullable(httpServletRequest.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> "token".equals(c.getName()))
                        .findFirst())
                .map(Cookie::getValue)
                .orElse("null");

        if(!jwtProvider.isValidToken(token)){
            return token;
        }

        final TokenBody tokenBody = jwtProvider.extractBody(token);
        return String.format("%s %s %s",
                tokenBody.role(), tokenBody.name(), tokenBody.email()
        );
    }


}
