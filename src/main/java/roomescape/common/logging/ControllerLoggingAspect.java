package roomescape.common.logging;

import java.util.Arrays;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Around("execution(* roomescape..*Controller.*(..))")
    public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("➡️ [진입] {} with args={}", method, Arrays.toString(args));

        Object result = joinPoint.proceed();

        log.info("⬅️ [응답] {} result={}", method, toNeedString(result));
        return result;
    }

    private String toNeedString(Object result) {
        if (result instanceof Collection<?> collection) {
            return "Collection(size=" + collection.size() + ")";
        }
        return String.valueOf(result);
    }
}
