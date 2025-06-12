package roomescape.logging;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;
import roomescape.auth.JwtProvider;
import roomescape.auth.TokenBody;
import roomescape.exception.custom.status.CustomException;

@Component
@RequiredArgsConstructor
public class LogUtil {
    private final JwtProvider jwtProvider;

    public String formatRequestInformation(HttpServletRequest httpServletRequest) {
        final String ip = httpServletRequest.getRemoteAddr();
        final int port = httpServletRequest.getRemotePort();
        final String formattedAddress = formatWithBrackets("IP", ip, port);

        final String tokenBody = getTokenBody(httpServletRequest);
        final String formattedTokenBody = formatWithBrackets("Token", tokenBody);

        final String requestURI = httpServletRequest.getRequestURI();
        final String formattedRequestURI = formatWithBrackets("URI", requestURI);

        return String.format("%s %s %s",
                formattedAddress,
                formattedTokenBody,
                formattedRequestURI);
    }

    public String formatMethodInformation(String methodName, String[] methodArgs) {
        final String params = getParams(methodArgs);
        return String.format("%s %s",
                formatWithBrackets("MethodName", methodName),
                formatWithBrackets("MethodParams", params)
        );
    }

    public String formatMethodInformation(JoinPoint joinPoint) {
        final String methodName = joinPoint.getSignature().toShortString();
        final String params = getParams(joinPoint.getArgs());

        return String.format("%s %s",
                formatWithBrackets("MethodName", methodName),
                formatWithBrackets("MethodParams", params)
        );
    }

    public String formatDurationMillis(long durationMillis) {
        return formatWithBrackets("DurationMillis", durationMillis);
    }

    public Object formatExceptionInformation(Exception exception) {
        String formattedExceptionName = formatWithBrackets("ExceptionClass", exception.getClass().getName());
        String formattedExceptionMessage = formatWithBrackets("ExceptionMessage", exception.getMessage());

        return String.format("%s %s", formattedExceptionName, formattedExceptionMessage);
    }

    public String getTokenBody(HttpServletRequest httpServletRequest) {
        final String token = Optional.ofNullable(httpServletRequest.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> "token".equals(c.getName()))
                        .findFirst())
                .map(Cookie::getValue)
                .orElse("null");

        if (!jwtProvider.isValidToken(token)) {
            return token;
        }

        final TokenBody tokenBody = jwtProvider.extractBody(token);
        return String.format("%s %s %s",
                tokenBody.role(), tokenBody.name(), tokenBody.email()
        );
    }

    private String formatWithBrackets(String key, Object... values) {
        String joinedValue = Arrays.stream(values)
                .map(String::valueOf) // null-safe toString
                .collect(Collectors.joining(" "));

        return String.format("[%s:%s]", key, joinedValue);
    }

    private String getParams(final Object[] args) {
        return Arrays.stream(args)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}
