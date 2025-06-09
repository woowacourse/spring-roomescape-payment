package roomescape.common.logging;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Around("execution(* roomescape..*Controller.*(..)) && " +
            "!execution(* roomescape.auth.controller.AuthController.*(..)) && " +
            "!execution(* roomescape.ui.ViewController.*(..))")
    public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        String maskedArgs = Arrays.stream(args)
                .map(this::maskIfSensitive)
                .collect(Collectors.joining(", ", "[", "]"));

        log.info("[진입] {} with args={}", method, maskedArgs);

        Object result = joinPoint.proceed();

        log.info("[응답] {} result={}", method, toNeedString(result));

        return result;
    }

    private String maskIfSensitive(Object arg) {
        if (arg instanceof String stringArg) {
            if (isJwtToken(stringArg)) {
                return "****(JWT_TOKEN)****";
            }
        }
        return String.valueOf(arg);
    }

    private boolean isJwtToken(String value) {
        // 헤더.payload.서명 구조 체크
        return value.split("\\.").length == 3 && value.length() > 30;
    }

    private String toNeedString(Object result) {
        if (result instanceof Collection<?> collection) {
            return "Collection(size=" + collection.size() + ")";
        }
        return String.valueOf(result);
    }
}
