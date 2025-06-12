package roomescape.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final HttpServletRequest httpServletRequest;
    private final LogUtil logUtil;

    @Around("@within(org.springframework.stereotype.Service) && (execution(* create*(..)) || execution(* delete*(..)))")
    public Object logging(final ProceedingJoinPoint joinPoint) throws Throwable {
        String requestInformation = logUtil.formatRequestInformation(httpServletRequest);
        String methodInformation = logUtil.formatMethodInformation(joinPoint);

        final long startMillis = System.currentTimeMillis();
        final Object result = joinPoint.proceed();
        final String durationInformation = logUtil.formatDurationMillis(System.currentTimeMillis() - startMillis);

        log.info("{} {} {}",
                requestInformation, methodInformation, durationInformation);

        return result;
    }
}
