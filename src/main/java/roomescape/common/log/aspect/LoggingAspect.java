package roomescape.common.log.aspect;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import roomescape.common.log.context.RequestContext;
import roomescape.common.log.context.RequestIdProvider;
import roomescape.common.log.message.LogMessageProvider;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final String HANDLER_NAME_FORMAT = "%s#%s";

    private final RequestIdProvider requestIdProvider;
    private final HttpServletRequest httpServletRequest;
    private final LogMessageProvider logMessageProvider;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void getMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    private void deleteMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    private void patchMapping() {
    }

    @Pointcut("getMapping() || postMapping() || putMapping() || deleteMapping() || patchMapping()")
    private void allMapping() {
    }

    @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    private void controllerPointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    private void exceptionHandlerCut() {
    }

    @Before("allMapping()")
    public void requestLog(final JoinPoint joinPoint) {
        String message = logMessageProvider.getRequestLog(
                RequestContext.get(
                        requestIdProvider.getId().toString(),
                        httpServletRequest,
                        formatHandlerName(joinPoint)
                ),
                getHandlerArguments(joinPoint)
        );

        log.info(message);
    }

    @AfterReturning(value = "controllerPointCut() || exceptionHandlerCut()", returning = "response")
    public void responseLog(final JoinPoint joinPoint, final ResponseEntity<?> response) {
        String message = logMessageProvider.getResponseLog(
                RequestContext.get(
                        requestIdProvider.getId().toString(),
                        httpServletRequest,
                        formatHandlerName(joinPoint)
                ),
                response
        );

        log.info(message);
    }

    private String formatHandlerName(final JoinPoint joinPoint) {
        return String.format(
                HANDLER_NAME_FORMAT,
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName()
        );
    }

    private Map<String, Object> getHandlerArguments(final JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        return IntStream.range(0, parameterNames.length)
                .boxed()
                .collect(Collectors.toMap(i -> parameterNames[i], i -> parameterValues[i]));
    }
}
